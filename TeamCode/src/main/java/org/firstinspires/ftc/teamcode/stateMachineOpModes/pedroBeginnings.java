package org.firstinspires.ftc.teamcode.stateMachineOpModes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class pedroBeginnings {
    private DcMotorEx spindexifier;

    private DcMotorEx shootMotor;

    private Servo loader1;

    private Servo loader2;

    private ElapsedTime stateTime;

    private enum StatesOfShooting {
        IDLE,
        SPINUP,
        LAUNCH,
        RESET_RELOAD
    }

    private StatesOfShooting launchState;

    private int index;

    // GATE_CONSTANTS.json

    private int GATE_CLOSE_ANGLE = 0;
    private int GATE_OPEN_ANGLE = 90;
    private double GATE_OPEN_TIME = 0.4;
    private double GATE_CLOSE_TIME = 0.4;

    // SHOOTER_CONSTANTS.yaml

    private int shotsRemaining = 0;

    private double flywheelVelocity = 0;
    private double MIN_FLYWHEEL_RPM = 800;
    private double TARGET_FLYWHEEL_RPM = 1100;
    private double FLYWHEEL_MAX_SPIN_UP_TIME = 2;

    private void init(HardwareMap hwMap) {
        shootMotor = hwMap.get(DcMotorEx.class, "shootMotor");
        spindexifier = hwMap.get(DcMotorEx.class, "spindexifier");

        loader1 = hwMap.get(Servo.class, "shootGate1");
        loader2 = hwMap.get(Servo.class, "shootGate2");

        launchState = StatesOfShooting.IDLE;

        shootMotor.setPower(0);

        spindexifier.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        spindexifier.setTargetPosition(GATE_CLOSE_ANGLE);
    }

    public void update() {
        switch (launchState) {
            case IDLE: {
                if (shotsRemaining > 0) {
                    spindexifier.setTargetPosition(GATE_OPEN_ANGLE * index);
                    // set the velocity for s h o o t e r
                }
            }
        }
    }
}
