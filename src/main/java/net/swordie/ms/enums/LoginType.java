package net.swordie.ms.enums;

public enum LoginType {
    HAVING_TROUBLE_LOGGIN_IN(0),
    SUCCESS(0),
    BLOCKED_FOR_HACKING(2),
    DELETED_OR_BLOCKED(3),
    INVALID_PASSWORD(4),
    HAVING_TROUBLE_LOGGIN_IN_2(6),
    REGION_BLOCK(0xE),
    HAVING_TROUBLE_LOGGIN_IN_3(0x17),
    CANNOT_CLICK_BUTTON(0x19),
    CANNOT_CLICK_BUTTON_2(0x1A),
    CANNOT_CLICK_BUTTON_3(0x1D),
    CANNOT_CLICK_BUTTON_4(0x1B), // 38
    CANNOT_CLICK_BUTTON_5(0x24),
    CANNOT_CLICK_BUTTON_6(0x23),
    NOT_A_REGISTERED_ID(5),
    MUST_BE_20(0xB),
    HAVING_TROUBLE(0xC),
    NOT_REGISTERED_WITH_LINK(0xF),
    NEEDS_VERIFICATION(0X10),
    IDK_4(0X12), // 38
    NOTHING(0X14),
    NOTHING_2(0X26),
    NOTHING_3(0X33),
    USE_MAPLE_OR_EMAIL_ID(0X29),
    NOTHING_4(0X2A),
    AVAILABLE_UNTIL(0X53),
    MUST_CREATE_PIC(0X35),
    NOT(0X36),
    NOTT(0X2E),
    NOTTT(0X54),
    OVERSEAS_BLOCKED(0X40),
    IDK_16(0X41),
    IDK_17(0X4A),
    IDK_18(0X5D),
    IDK_19(0X45),
    TROUBLE_LOGGING_IN(0X8),
    ACCOUNT_LIST(0x2B),
    CANNOT_DELETE_CHAR_ON_MAPLE_TOGETHER(0x2D),

    ;

    /**
     * Created on 4/30/2017.
     */
    private byte value;

    LoginType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
