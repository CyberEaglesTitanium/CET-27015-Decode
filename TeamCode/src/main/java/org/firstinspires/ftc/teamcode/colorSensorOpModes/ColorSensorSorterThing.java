package org.firstinspires.ftc.teamcode.colorSensorOpModes;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@TeleOp(name = "Color Sorter Test OpMode")
public class ColorSensorSorterThing extends LinearOpMode {
    private NormalizedColorSensor colsense;
    private DcMotorEx sweepingSweeper;

    @Override
    public void runOpMode() {
        colsense = hardwareMap.get(NormalizedColorSensor.class, "colsense");
        sweepingSweeper = hardwareMap.get(DcMotorEx.class, "spindexifier");
        sweepingSweeper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sweepingSweeper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        waitForStart();

        NormalizedRGBA colors = colsense.getNormalizedColors();

        while (colors.blue >= 100 && colors.red <= 162) {
            telemetry.addLine("Color is PURPLE!");
            sweepingSweeper.setPower(1);
        }
        while (colors.green == 255) {
            telemetry.addLine("Color is GREEN!");
            sweepingSweeper.setPower(0);
        }
        while (colors.blue == 0 && colors.red == 0 && colors.green == 0) {
            telemetry.addLine("No Colors found :c");
        }
    }
}
