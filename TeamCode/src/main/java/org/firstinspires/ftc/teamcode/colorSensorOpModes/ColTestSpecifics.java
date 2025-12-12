package org.firstinspires.ftc.teamcode.colorSensorOpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp
public class ColTestSpecifics extends LinearOpMode {
    private ColorSensor colsense;
    private DcMotorEx spindexifier;

    int redColor;
    int greenColor;
    int blueColor;

    void redColorSensor() {
        redColor = colsense.red();
    }
    void greenColorSensor() {
        greenColor = colsense.green();
    }
    void blueColorSensor() {
        blueColor = colsense.blue();
    }

    void checkColor() {
        redColorSensor();
        greenColorSensor();
        blueColorSensor();
    }

    int colorValid() {
        if (greenColor == Range.clip(225, 200, 255)) {
            return 1;
        } else if (redColor == Range.clip(185, 170, 221) && blueColor == Range.clip(168, 155, 201)) {
            return 2;
        } else {
            return 0;
        }
    }

    @Override
    public void runOpMode() {
        colsense = hardwareMap.get(ColorSensor.class, "colsense");
        spindexifier = hardwareMap.get(DcMotorEx.class, "spindexifier");
        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("red", colsense.red());
            telemetry.addData("green", colsense.green());

            checkColor();

            int colorCheck = colorValid();

            telemetry.addData("color check", colorCheck);
            telemetry.update();
        }

    }
}
