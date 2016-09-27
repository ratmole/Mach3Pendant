package gr.ratmole.android.Mach3Pendant.shared;


public class Handshake {
    public static final int PC_IM_READY = 1;
    public static final int PHONE_TRUST_ME = 2;
    public static final int PC_ENTER_PIN = 3;
    public static final int PHONE_SENT_PIN = 4;
    public static final int PC_I_TRUST_PHONE = 5;
    public static final int PC_INCORRECT_PIN = 6;
    int id = 0;
    String name, type;

    private String pin = "";
    private boolean remember = false;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public boolean isRemember() {
        return remember;
    }

    public Handshake phone_TrustMe(String name_, String type_) {
        name = name_;
        type = type_;
        id = PHONE_TRUST_ME;
        return this;
    }

    public Handshake pc_EnterPin() {
        id = PC_ENTER_PIN;
        return this;
    }

    public Handshake phone_SentPin(String pin_, boolean remember_) {
        id = PHONE_SENT_PIN;
        pin = pin_;
        remember = remember_;
        return this;
    }

    public Handshake pc_ITrustPhone() {
        id = PC_I_TRUST_PHONE;
        return this;
    }

    public Handshake pc_IncorrectPin() {
        id = PC_INCORRECT_PIN;
        return this;
    }
}
