package mx.cubiccoding.front.notifications.actions

class CommandPayloadAction(
    val action: String,
    val data: String
) : FirebasePayloadAction {
    override fun execute() {
        //TODO: Take an action based on this command
    }
}