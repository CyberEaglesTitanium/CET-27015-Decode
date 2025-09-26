package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.CRServo;
//import com.qualcomm.robotcore.hardware.ColorRangeSensor;
//import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.bylazar.configurables.PanelsConfigurables;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.configurables.annotations.IgnoreConfigurable;
import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;

import org.firstinspires.ftc.robotcore.external.Telemetry;


@TeleOp(name = "TeleOpStuff")
    public class TeleOpStuff extends LinearOpMode {

        private DcMotorEx frontLeft;
        private DcMotorEx frontRight;
        private DcMotorEx backLeft;
        private DcMotorEx backRight;

        // Init gamepad, motors + servo

        @Override
        public void runOpMode() {
            // Define all motors and servos
            frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
            frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
            backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
            backRight = hardwareMap.get(DcMotorEx.class, "backRight");
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            // Variables
            double ticks = 	537.7;
            int whuhPos1 = frontLeft.getCurrentPosition();
            int whuhPos2 = frontRight.getCurrentPosition();
            int whuhPos3 = backLeft.getCurrentPosition();
            int whuhPos4 = backRight.getCurrentPosition();

            // Put initialization blocks here.
            frontLeft.setDirection(DcMotor.Direction.REVERSE);
            backLeft.setDirection(DcMotor.Direction.REVERSE);




            // Main loop for the motors
            waitForStart();
            while (opModeIsActive()) {

                double leftFrontPower;
                double rightFrontPower;
                double leftBackPower;
                double rightBackPower;
                whuhPos1 = frontLeft.getCurrentPosition();
                whuhPos2 = frontRight.getCurrentPosition();
                whuhPos3 = backLeft.getCurrentPosition();
                whuhPos4 = backRight.getCurrentPosition();

                telemetry.addData("FrontLeftMotor Position", whuhPos1);
                telemetry.addData("FrontRightMotor Position", whuhPos2);
                telemetry.addData("BackLeftMotor Position", whuhPos3);
                telemetry.addData("BackRightMotor Position", whuhPos4);
                // Gamepad movement code
                double drive = -gamepad1.left_stick_y;
                double strafe = gamepad1.left_stick_x;
                double turn = gamepad1.right_stick_x;
                leftFrontPower = Range.clip(drive + turn + strafe, -1, 1);
                rightFrontPower = Range.clip(drive - turn - strafe, -1, 1);
                leftBackPower = Range.clip(drive + turn - strafe, -1, 1);
                rightBackPower = Range.clip(drive - turn + strafe, -1, 1);
                if (gamepad1.left_bumper) {
                    leftFrontPower /= 2;
                    leftBackPower /= 2;
                    rightFrontPower /= 2;
                    rightBackPower /= 2 ;
                }

                if (gamepad1.right_bumper) {
                    leftFrontPower *= 1.8;
                    leftBackPower *= 1.8;
                    rightFrontPower *= 1.8;
                    rightBackPower *= 1.8;
                }

                frontLeft.setPower(leftFrontPower /= 1.8);
                frontRight.setPower(rightFrontPower /= 1.8);
                backLeft.setPower(leftBackPower /= 1.8);
                backRight.setPower(rightBackPower /= 1.8);
            }
        }
    }
