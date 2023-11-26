package io.army.session;


public final class TimeoutException extends SessionException {

    private final long overspendMills;

    public TimeoutException(String message, long overspendMills) {
        super(message);
        this.overspendMills = overspendMills;
    }

    public TimeoutException(String message, Throwable cause, long overspendMills) {
        super(message, cause);
        this.overspendMills = overspendMills;
    }

    public TimeoutException(Throwable cause, long overspendMills) {
        super(cause);
        this.overspendMills = overspendMills;
    }

    public final long getOverspendMills() {
        return this.overspendMills;
    }

}
