package org.firstinspires.ftc.teamcode.testOpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;


@TeleOp(name = "Get Target Ticks From Spindexifier")
    public class TestTargetTicksWithGamepad extends LinearOpMode {

        private DcMotorEx spindexifier;
        int index;

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
            while (opModeIsActive()) {
                if (gamepad2.rightBumperWasReleased()) {
                    spinUseRight();
                }

                telemetry.addData("Spindexer Position",spindexifier.getCurrentPosition());
                telemetry.update();
            }
        }
    }
