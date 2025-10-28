package org.firstinspires.ftc.teamcode.colorSensorOpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

@TeleOp
public class ColTest extends LinearOpMode {
    private ColorSensor colsense;

    @Override
    public void runOpMode() {
        colsense = hardwareMap.get(ColorSensor.class, "colsense");

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Light Detected", ((OpticalDistanceSensor) colsense).getLightDetected());

            //Determining the amount of red, green, and blue
            telemetry.addData("Red", colsense.red());
            telemetry.addData("Green", colsense.green());
            telemetry.addData("Blue", colsense.blue());
            telemetry.update();
        }
    }
}
