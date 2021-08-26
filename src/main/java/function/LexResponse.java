package function;

public class LexResponse
{
    private DialogAction dialogAction;

    public LexResponse() {

    }

    public LexResponse(DialogAction dialogAction)
    {
        this.dialogAction = dialogAction;
    }

    public DialogAction getDialogAction()
    {
        return dialogAction;
    }

    public void setDialogAction(DialogAction dialogAction)
    {
        this.dialogAction = dialogAction;
    }

    public LexResponse initiate()
    {
        Message message = new Message("PlainText", "Some response Message");
        this.dialogAction = new DialogAction("Close", "Fulfilled", message);
        return this;
    }
}
