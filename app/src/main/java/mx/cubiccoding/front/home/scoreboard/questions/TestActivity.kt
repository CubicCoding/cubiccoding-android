package mx.cubiccoding.front.home.scoreboard.questions

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.cubiccoding.R
import mx.cubiccoding.front.home.scoreboard.questions.recyclerview.QuestionOptionsAdapter
import mx.cubiccoding.front.utils.views.ConfirmActionDialog
import mx.cubiccoding.front.utils.views.showFancyToast
import mx.cubiccoding.model.networking.calls.ScoreboardRequest
import mx.cubiccoding.model.utils.Constants
import mx.cubiccoding.model.workers.UploadAnswerWorker
import mx.cubiccoding.persistence.database.CubicCodingDB
import mx.cubiccoding.persistence.database.questions.QuestionEntity
import org.json.JSONArray
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TestActivity: AppCompatActivity() {

    companion object {
        const val TEST_ID_EXTRA = "test.id.extra"
    }

    private val adapter by lazy { QuestionOptionsAdapter() }
    private var questionInfo: QuestionEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        setupViews()
    }

    private fun setupViews() {
        questionContent.movementMethod = ScrollingMovementMethod()

        optionsRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        optionsRecyclerView.adapter = adapter

        uploadAnswers.setOnClickListener {
            val optionsSelected = adapter.getOptionsSelected()
            if (optionsSelected.isEmpty()) {
                showFancyToast(this@TestActivity, getString(R.string.select_an_option))
            } else {
                val testUuid = intent?.getStringExtra(TEST_ID_EXTRA) ?: ""
                val answers = optionsSelected.toString()
                with(ConfirmActionDialog(this@TestActivity)) {
                    setContentMessage(getString(R.string.you_have_selected_values, answers))
                    setConfirmationButtonLabel(getString(R.string.confirm)) {dialog ->
                        dialog.dismiss()
                        provideFeedback()
                        uploadAnswer(testUuid, answers)
                        disableUploadAnswersButton()

                    }
                }.show()
            }
        }

        fetchTest()
    }

    private fun disableUploadAnswersButton() {
        uploadAnswers.isEnabled = false
        uploadAnswers.alpha = 0.5F
    }

    private fun uploadAnswer(testUuid: String, answers: String) {

        //TODO: Update isAnswered column...

        if (testUuid.isEmpty() || answers.isEmpty()) {
            showFancyToast(this, getString(R.string.missing_info_try_again_later))
        } else {
            val inputData = workDataOf(
                Constants.TEST_UUID_WORKER_INPUT to testUuid,
                Constants.ANSWERS_WORKER_INPUT to answers
            )

            //Always start by setting up the upload job to the server (notice it will delay one min and a half before actually kicking off...)
            val worker = with(OneTimeWorkRequestBuilder<UploadAnswerWorker>()) {
                setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                setInputData(inputData)
                setInitialDelay(Constants.UPLOAD_ANSWER_DELAY_IN_MS, TimeUnit.MILLISECONDS)
            }.build()
            val workManager = WorkManager.getInstance(this)
            workManager.beginUniqueWork(Constants.UPLOAD_ANSWER_WORK_NAME, ExistingWorkPolicy.KEEP, worker).enqueue()
            Timber.e("Track, Scheduled a work to be executed for uploading the answer...")

            //Try to upload the answer immediately, if it works cancel the previous job, otherwise rely on the work manager to get it done when possible...
            lifecycleScope.launch(Dispatchers.IO) {

                //Is fair to mark now the question as answered, since it is enqueued and will immediately be uploaded if possible...
                CubicCodingDB.getDatabaseInstance().getQuestionDao().updateIsAnswered(testUuid, true)

                val response = ScoreboardRequest.uploadAnswer(testUuid, answers)
                if (response == ScoreboardRequest.UploadAnswerStatus.SUCCESS) {//Only if immediate upload is successful then cancel the enqueued work, otherwise move on...
                    Timber.e("Track, Immediate upload answer worked, cancel the pending task...")
                    workManager.cancelWorkById(worker.id)
                }
            }
        }
    }

    private fun provideFeedback() {
        val score = adapter.provideFeedback() * (questionInfo?.maxScore ?: 0)
        showFancyToast(this, getString(R.string.received_score_value, score.toString()))
        scoreAwardedLabel.visibility = View.VISIBLE
        scoreAwardedLabel.text = getString(R.string.received_score_value, score.toString())
    }

    private fun fetchTest() {
        //This is only safe because is an async call to a DB, network calls should contain leak protection logic...
        intent?.getStringExtra(TEST_ID_EXTRA)?.let { testUuid ->
            lifecycleScope.launch(Dispatchers.IO) {
                val questionInfo = CubicCodingDB.getDatabaseInstance().getQuestionDao().getQuestion(testUuid)
                if (questionInfo == null) {//If we don't find the question for any reason, just close activity and leave a message
                    withContext(Dispatchers.Main) {
                        showFancyToast(this@TestActivity, getString(R.string.question_not_found))
                        this@TestActivity.finish()
                    }
                    return@launch
                }

                //convert String of options to actual array...
                val optionsJsonArray = JSONArray(questionInfo.options)
                val options = mutableListOf<QuestionOptionsAdapter.OptionItem>()
                for (index in 0 until optionsJsonArray.length()) {
                    options.add(QuestionOptionsAdapter.OptionItem(optionsJsonArray.getString(index)))
                }

                val answersJsonArray = JSONArray(questionInfo.answers)
                val answers = mutableListOf<Int>()
                for (index in 0 until answersJsonArray.length()) {
                    answers.add(answersJsonArray.getInt(index))
                }

                //Move back to the main thread to update views...
                withContext(Dispatchers.Main) {
                    populateQuestion(options, answers, questionInfo)
                }
            }
        }
    }

    private fun populateQuestion(options: List<QuestionOptionsAdapter.OptionItem>, answers: List<Int>, questionInfo: QuestionEntity) {
        adapter.populateOptions(options, answers)
        testId.text = getString(R.string.test_id_value, questionInfo.testUuid)
        maxScoreLabel.text = getString(R.string.max_score_value, questionInfo.maxScore.toString())
        questionContent.text = questionInfo.questionTitle
        this.questionInfo = questionInfo
    }
}
