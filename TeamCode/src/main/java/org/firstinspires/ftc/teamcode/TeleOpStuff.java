package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.CRServo;
//import com.qualcomm.robotcore.hardware.ColorRangeSensor;
//import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
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

        private DcMotorEx intakeMotor;
        private DcMotorEx shootMotor;

        private CRServo shootGate1;
        private CRServo shootGate2;
        // Init gamepad, motors + servo

        @Override
        public void runOpMode() {
            // Define all motors and servos
            frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
            frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
            backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
            backRight = hardwareMap.get(DcMotorEx.class, "backRight");

            intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
            shootMotor = hardwareMap.get(DcMotorEx.class, "shootMotor");

            shootGate1 = hardwareMap.get(CRServo.class, "shootGate1");
            shootGate2 = hardwareMap.get(CRServo.class, "shootGate2");

            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            // Variables
            double ticks = 	537.7;

            double shooterSpeed = 0;

            int whuhPos1 = frontLeft.getCurrentPosition();
            int whuhPos2 = frontRight.getCurrentPosition();
            int whuhPos3 = backLeft.getCurrentPosition();
            int whuhPos4 = backRight.getCurrentPosition();
            int intakePos = intakeMotor.getCurrentPosition();

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

                // Gamepad movement code
                double drive = -gamepad1.left_stick_y;
                double strafe = gamepad1.left_stick_x;
                double turn = gamepad1.right_stick_x;
                leftFrontPower = Range.clip(drive + turn + strafe, -1, 1);
                rightFrontPower = Range.clip(drive - turn - strafe, -1, 1);
                leftBackPower = Range.clip(drive + turn - strafe, -1, 1);
                rightBackPower = Range.clip(drive - turn + strafe, -1, 1);

                // Speed control buttons
                if (gamepad1.left_bumper) {
                    leftFrontPower /= 2;
                    leftBackPower /= 2;
                    rightFrontPower /= 2;
                    rightBackPower /= 2;
                }

                if (gamepad1.right_bumper) {
                    leftFrontPower *= 1.8;
                    leftBackPower *= 1.8;
                    rightFrontPower *= 1.8;
                    rightBackPower *= 1.8;
                }

                // Sets the power when gamepad
                frontLeft.setPower(leftFrontPower /= 1.8);
                frontRight.setPower(rightFrontPower /= 1.8);
                backLeft.setPower(leftBackPower /= 1.8);
                backRight.setPower(rightBackPower /= 1.8);

                telemetry.addData("FrontLeftMotor Speed", leftFrontPower);
                telemetry.addData("FrontRightMotor Speed", rightFrontPower);
                telemetry.addData("BackLeftMotor Speed", leftBackPower);
                telemetry.addData("BackRightMotor Speed", rightBackPower);
                telemetry.update();

                // Intake controls (in)
                if (gamepad2.right_trigger >= 0.5) {
                        intakeMotor.setPower(1);
                } else {
                    intakeMotor.setPower(0);
                }
                if (gamepad2.dpad_up) {
                    intakeMotor.setPower(1);
                } else {
                    intakeMotor.setPower(0);
                }

                // Intake controls (out)
                if (gamepad2.left_trigger >= 0.5) {
                        intakeMotor.setPower(-1);
                } else {
                    intakeMotor.setPower(0);
                }
                if (gamepad2.dpad_down) {
                    intakeMotor.setPower(-1);
                } else {
                    intakeMotor.setPower(0);
                }

                // Shooter controls (Controller 2)

                if (gamepad2.x) {
                    shootMotor.setPower(1);
                } else {
                    shootMotor.setPower(0);
                }

//                if (gamepad2.right_bumper) {
//                    if (shooterSpeed > 1) {
//                        shooterSpeed = 1;
//                    } else {
//                        shooterSpeed += 0.1;
//                    }
//                } else if (gamepad2.left_bumper) {
//                    if (shooterSpeed < 0.1) {
//                        shooterSpeed = 0.1;
//                    } else {
//                        shooterSpeed -= 0.1;
//                    }
//                }

                if (gamepad2.a) {
                    shootGate1.setPower(0.7);
                    shootGate2.setPower(0.7);
                } else {
                    shootGate1.setPower(0);
                    shootGate2.setPower(0);
                }
                if (gamepad2.b) {
                    shootGate1.setPower(-0.7);
                    shootGate1.setPower(-0.7);
                } else {
                    shootGate1.setPower(0);
                    shootGate2.setPower(0);
                }

            }
        }
    }
