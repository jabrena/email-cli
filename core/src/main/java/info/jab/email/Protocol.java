package info.jab.email;

public enum Protocol {
    IMAP("imap"),
    POP3("pop3"),
    SMTP("smtp");

    private final String value;

    Protocol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
