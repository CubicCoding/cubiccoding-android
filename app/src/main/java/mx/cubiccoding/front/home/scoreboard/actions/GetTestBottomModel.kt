package mx.cubiccoding.front.home.scoreboard.actions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.cubiccoding.front.mvp.BaseMVPModel
import mx.cubiccoding.model.dtos.GetTestResponsePayload
import mx.cubiccoding.model.networking.GenericRequestListener
import mx.cubiccoding.model.networking.calls.VoucherRequest
import mx.cubiccoding.model.dtos.GetVoucherResponsePayload
import mx.cubiccoding.model.networking.CubicCodingRequestException
import mx.cubiccoding.model.networking.RequestErrorType
import mx.cubiccoding.model.networking.calls.ScoreboardRequest
import mx.cubiccoding.persistence.database.CubicCodingDB

class GetTestBottomModel: BaseMVPModel() {

    fun getTestQuestion(uuid: String, callback: GenericRequestListener<String, Throwable>) {
        launch(Dispatchers.IO) {
            val question = CubicCodingDB.getDatabaseInstance().getQuestionDao().getQuestion(uuid)
            if (question?.isAnswered == true) {//Question is already answered in our records, notify user about this...
                callback.onFail(CubicCodingRequestException("Question already answered", RequestErrorType.QUESTION_ALREADY_ANSWERED))
            } else {
                ScoreboardRequest.getTestQuestion(uuid, callback)
            }
        }
    }
}
