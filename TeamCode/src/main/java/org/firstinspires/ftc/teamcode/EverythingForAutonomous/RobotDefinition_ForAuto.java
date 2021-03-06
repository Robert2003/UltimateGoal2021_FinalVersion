package org.firstinspires.ftc.teamcode.EverythingForAutonomous;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;

import static java.lang.Thread.sleep;

public class RobotDefinition_ForAuto
{
    public DcMotor intake1   = null, intake2 = null;
    public DcMotor wobbleArm = null;
    public DcMotorEx flyWheel  = null;

    public Servo servo, wobbleServo, intakeServo;

    public double drive;
    public double strafe;
    public double rotate;

    public static PIDFCoefficients MOTOR_VELO_PID = new PIDFCoefficients(55, 0, 10, 12.7);

    public static double MOTOR_TICKS_PER_REV = 28;
    public static double MOTOR_GEAR_RATIO = 1;

    public static int TOWER_RPM = 3200;
    public static int POWERSHOTS_RPM = 2800;

    HardwareMap hardwareMap = null;
    private ElapsedTime period = new ElapsedTime();

    public RobotDefinition_ForAuto(){}

    public void init(HardwareMap ahwMap)
    {
        hardwareMap = ahwMap;

        intake1   = hardwareMap.get(DcMotor.class, "intake1");
        intake2   = hardwareMap.get(DcMotor.class, "intake2");
        flyWheel  = hardwareMap.get(DcMotorEx.class, "flyWheel");
        wobbleArm = hardwareMap.get(DcMotor.class, "wobbleArm");

        intake2.setDirection(DcMotor.Direction.REVERSE);
        wobbleArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wobbleArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        flyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        servo       = hardwareMap.get(Servo.class, "servo");
        wobbleServo = hardwareMap.get(Servo.class, "wobbleServo");
        intakeServo = hardwareMap.get(Servo.class, "intakeServo");
        servo.setPosition(0.55);
        intakeServo.setPosition(1);
        wobbleServo.setPosition(0.6);

        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
        MotorConfigurationType motorConfigurationType = flyWheel.getMotorType().clone();
        motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
        flyWheel.setMotorType(motorConfigurationType);

        flyWheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                MOTOR_VELO_PID.p, MOTOR_VELO_PID.i, MOTOR_VELO_PID.d,
                MOTOR_VELO_PID.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
        ));
    }

    public void shootrings(int timesToShoot) throws InterruptedException {
        for(int i = 1; i <= timesToShoot; i++) {
            servo.setPosition(0);
            sleep(400);
            servo.setPosition(0.55);
            sleep(400);
        }
    }

    public void shootrings(int timesToShoot, int delay) throws InterruptedException {
        for(int i = 1; i <= timesToShoot; i++) {
            servo.setPosition(0);
            sleep(delay);
            servo.setPosition(0.55);
            sleep(delay);
        }
    }

    public void dropArm(int ticks)
    {
        wobbleArm.setTargetPosition(ticks);
        wobbleArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        wobbleArm.setPower(0.3);
        while(Math.abs(wobbleArm.getTargetPosition()-wobbleArm.getCurrentPosition()) > 10);
    }

    public void dropWobble() throws InterruptedException {
        wobbleServo.setPosition(0);
        sleep(400);
    }

    public void grabWobble() throws InterruptedException {
        wobbleServo.setPosition(0.53);
        sleep(400);
        wobbleArm.setTargetPosition(670);
        wobbleArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        wobbleArm.setPower(0.5);
        while(Math.abs(wobbleArm.getTargetPosition()-wobbleArm.getCurrentPosition()) > 10);
    }

    public void toggleFlyWheel(boolean shouldTurnOn)
    {
        if(shouldTurnOn==true)
            flyWheel.setVelocity(rpmToTicksPerSecond(TOWER_RPM));
        else
            flyWheel.setVelocity(rpmToTicksPerSecond(10));
    }

    public void toggleFlyWheel(boolean shouldTurnOn, int RPM)
    {
        if(shouldTurnOn==true)
            flyWheel.setVelocity(rpmToTicksPerSecond(RPM));
        else
            flyWheel.setVelocity(rpmToTicksPerSecond(10));
    }

    public void toggleIntake()
    {
        if(intake1.getPower()!=0)
        {
            intake1.setPower(0);
            intake2.setPower(0);
        }
        else
        {
            intake1.setPower(1);
            intake2.setPower(1);
        }
    }

    public void toggleIntakeServo(boolean shouldGoDown)
    {
        if(!shouldGoDown)
            intakeServo.setPosition(1);
        else
            intakeServo.setPosition(0.42);
    }

    public static double rpmToTicksPerSecond(double rpm) {
        return rpm * MOTOR_TICKS_PER_REV / MOTOR_GEAR_RATIO / 60;
    }

 }

