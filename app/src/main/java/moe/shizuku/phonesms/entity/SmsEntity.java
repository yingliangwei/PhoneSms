package moe.shizuku.phonesms.entity;

public class SmsEntity {
    public String title;
    public String message;
    public long time;
    public int sim;
    public int isSend;
    public int sms_send;

    public SmsEntity(String title, String message, long time, int sim, int send, int sms_send) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.sim = sim;
        this.isSend = send;
        this.sms_send = sms_send;
    }
}
