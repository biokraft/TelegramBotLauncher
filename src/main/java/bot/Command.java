package bot;

public class Command {

    private String trigger, response;

    public Command(String trigger, String response) {
        this.response = response;
        this.trigger = trigger;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getResponse() {
        return response;
    }
}
