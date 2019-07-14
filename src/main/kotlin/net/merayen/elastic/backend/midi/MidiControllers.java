package net.merayen.elastic.backend.midi;

/**
 * Second byte in the MIDI packet.
 * Control Change Messages (Data Bytes)
 */
public class MidiControllers {
	public static final short BANK_SELECT_MSB = 0;
	public static final short MODULATION_WHEEL_MSB = 1;
	public static final short BREATH_CONTROLLER_MSB = 2;
	public static final short FOOT_CONTROLLER_MSB = 4;
	public static final short PORTAMENTO_TIME_MSB = 5;
	public static final short DATA_ENTRY_MSB = 6;
	public static final short CHANNEL_VOLUME_MSB = 7;
	public static final short BALANCE_MSB = 8;
	public static final short PAN_MSB = 10;
	public static final short EXPRESSION_MSB = 11;
	public static final short EFFECT_CONTROL_1_MSB = 12;
	public static final short EFFECT_CONTROL_2_MSB = 13;
	public static final short GPC_1_MSB = 16; // General purpose controller
	public static final short GPC_2_MSB = 17;
	public static final short GPC_3_MSB = 18;
	public static final short GPC_4_MSB = 19;
	public static final short BANK_SELECT_LSB = 32;
	public static final short MODULATION_WHEEL_LSB = 33;
	public static final short BREATH_CONTROLLER_LSB = 34;
	public static final short FOOT_CONTROLLER_LSB = 36;
	public static final short PORTAMENTO_TIME_LSB = 37;
	public static final short DATA_ENTRY_LSB = 38;
	public static final short CHANNEL_VOLUME_LSB = 39;
	public static final short BALANCE_LSB = 40;
	public static final short PAN_LSB = 42;
	public static final short EXPRESSION_LSB = 43;
	public static final short EFFECT_CONTROL_1_LSB = 44;
	public static final short EFFECT_CONTROL_2_LSB = 45;
	public static final short GPC_1_LSB = 48;
	public static final short GPC_2_LSB = 49;
	public static final short GPC_3_LSB = 50;
	public static final short GPC_4_LSB = 51;
	public static final short SUSTAIN = 64;
	public static final short RPN_LSB = 100;
	public static final short RPN_MSB = 101;
	public static final short ALL_SOUND_OFF = 120;
	public static final short RESET_ALL_CONTROLLERS = 121;
	public static final short LOCAL_CONTROL = 122;
	public static final short ALL_NOTES_OFF = 123;
	public static final short OMNI_MODE_OFF = 124;
	public static final short OMNI_MODE_ON = 125;
	public static final short POLY_MODE_OFF = 126;
	public static final short POLY_MODE_ON = 127;
}
