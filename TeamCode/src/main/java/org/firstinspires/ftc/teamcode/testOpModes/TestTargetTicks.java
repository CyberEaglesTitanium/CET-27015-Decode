package org.firstinspires.ftc.teamcode.testOpModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Disabled
@Autonomous(name = "Test Target Position From Spindexifier")
    public class TestTargetTicks extends LinearOpMode {

        private DcMotorEx spindexifier;
        int index;

        void moveToPos() {
            spindexifier.setTargetPosition(-178);
            spindexifier.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            spindexifier.setPower(1);
            sleep(2000);

            spindexifier.setTargetPosition(-534);
            spindexifier.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            spindexifier.setPower(1);
            sleep(2000);
            spindexifier.setPower(0);
        }

    void spinIndex() {
        spindexifier.setTargetPosition(-178 * index);
    }

    void resetSpindexEncoder() {
        spindexifier.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    void spinUseRight() {
        if (index == 3) {
            index = 1;
        } else {
            index += 1;
        }
        spinIndex();
        spindexifier.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        spindexifier.setPower(1);
    }

        @Override
        public void runOpMode() {
            // Define all motors and servos
            spindexifier = hardwareMap.get(DcMotorEx.class, "test");
            spindexifier.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            // Main loop for the motors
            waitForStart();
            spinUseRight();
            sleep(2000);
            spinUseRight();
            sleep(2000);
            spinUseRight();
        }
    }
