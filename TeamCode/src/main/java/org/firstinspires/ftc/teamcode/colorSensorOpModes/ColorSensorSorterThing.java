package org.firstinspires.ftc.teamcode.colorSensorOpModes;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ColorSensorSorterThing extends LinearOpMode {
    private NormalizedColorSensor colsense;
    private Servo sweepingSweeper;

    @Override
    public void runOpMode() {
        colsense = hardwareMap.get(NormalizedColorSensor.class, "colsense");
        sweepingSweeper = hardwareMap.get(Servo.class, "sweepingSweeper");

        waitForStart();

        NormalizedRGBA colors = colsense.getNormalizedColors();

        while (colors.blue >= 100 && colors.red <= 162) {
            telemetry.addLine("Color is PURPLE!");
            sweepingSweeper.setPosition(1);
        }
        while (colors.green == 255) {
            telemetry.addLine("Color is GREEN!");
            sweepingSweeper.setPosition(0);
        }
        while (colors.blue == 0 && colors.red == 0 && colors.green == 0) {
            telemetry.addLine("No Colors found...");
        }
    }
}
