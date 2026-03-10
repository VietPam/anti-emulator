package diff.strazzere.anti;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.Menu;
import diff.strazzere.anti.debugger.FindDebugger;
import diff.strazzere.anti.emulator.FindEmulator;
import diff.strazzere.anti.monkey.FindMonkey;
import diff.strazzere.anti.taint.FindTaint;

public class MainActivity extends Activity {

    private TextView outputText;
    private StringBuilder outputBuffer = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputText = findViewById(R.id.outputText);

        new Thread() {
            @Override
            public void run() {
                super.run();

                isTaintTrackingDetected();
                isMonkeyDetected();
                isDebugged();
                isQEmuEnvDetected();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        outputText.setText(outputBuffer.toString());
                    }
                });
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean isQEmuEnvDetected() {

        log("");
        log("=== Checking Emulator Environment ===");

        log("    hasKnownDeviceId : " + FindEmulator.hasKnownDeviceId(getApplicationContext()));
        log("    hasKnownPhoneNumber : " + FindEmulator.hasKnownPhoneNumber(getApplicationContext()));
        log("    isOperatorNameAndroid : " + FindEmulator.isOperatorNameAndroid(getApplicationContext()));
        log("    hasKnownImsi : " + FindEmulator.hasKnownImsi(getApplicationContext()));
        log("    hasEmulatorBuild : " + FindEmulator.hasEmulatorBuild(getApplicationContext()));
        log("    hasPipes : " + FindEmulator.hasPipes());
        log("    hasQEmuDriver : " + FindEmulator.hasQEmuDrivers());
        log("    hasQEmuFiles : " + FindEmulator.hasQEmuFiles());
        log("    hasGenyFiles : " + FindEmulator.hasGenyFiles());
        log("    hasEmulatorAdb : " + FindEmulator.hasEmulatorAdb());

        for(String abi : Build.SUPPORTED_ABIS) {
            if (abi.equalsIgnoreCase("armeabi-v7a")) {
                log("    hitsQemuBreakpoint : " + FindEmulator.checkQemuBreakpoint());
            }
        }

        boolean detected =
                FindEmulator.hasKnownDeviceId(getApplicationContext())
                        || FindEmulator.hasKnownImsi(getApplicationContext())
                        || FindEmulator.hasEmulatorBuild(getApplicationContext())
                        || FindEmulator.hasKnownPhoneNumber(getApplicationContext())
                        || FindEmulator.hasPipes()
                        || FindEmulator.hasQEmuDrivers()
                        || FindEmulator.hasEmulatorAdb()
                        || FindEmulator.hasQEmuFiles()
                        || FindEmulator.hasGenyFiles();

        log("");
        log("    RESULT");

        if (detected) {
            log("        Emulator environment DETECTED");
        } else {
            log("        Emulator environment NOT detected");
        }

        return detected;
    }

    public boolean isTaintTrackingDetected() {

        log("");
        log("=== Checking Taint Tracking ===");

        log("    hasAppAnalysisPackage : " + FindTaint.hasAppAnalysisPackage(getApplicationContext()));
        log("    hasTaintClass : " + FindTaint.hasTaintClass());
        log("    hasTaintMemberVariables : " + FindTaint.hasTaintMemberVariables());

        boolean detected =
                FindTaint.hasAppAnalysisPackage(getApplicationContext())
                        || FindTaint.hasTaintClass()
                        || FindTaint.hasTaintMemberVariables();

        log("");
        log("    RESULT");

        if (detected) {
            log("        Taint tracking DETECTED");
        } else {
            log("        Taint tracking NOT detected");
        }

        return detected;
    }

    public boolean isMonkeyDetected() {

        log("");
        log("=== Checking Monkey User ===");

        log("    isUserAMonkey : " + FindMonkey.isUserAMonkey());

        boolean detected = FindMonkey.isUserAMonkey();

        log("");
        log("    RESULT");

        if (detected) {
            log("        Monkey user DETECTED");
        } else {
            log("        Monkey user NOT detected");
        }

        return detected;
    }

    public boolean isDebugged() {

        log("");
        log("=== Checking Debugger ===");

        boolean tracer = false;

        try {
            tracer = FindDebugger.hasTracerPid();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        log("    tracerPid : " + tracer);
        log("    isBeingDebugged : " + FindDebugger.isBeingDebugged());

        boolean detected = FindDebugger.isBeingDebugged() || tracer;

        log("");
        log("    RESULT");

        if (detected) {
            log("        Debugger DETECTED");
        } else {
            log("        No debugger detected");
        }

        return detected;
    }

    public void log(String msg) {
        Log.v("AntiEmulator", msg);
        outputBuffer.append(msg).append("\n");
    }
}