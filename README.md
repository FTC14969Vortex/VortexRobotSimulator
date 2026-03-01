# VortexRobotSimulator

A desktop simulator for **FTC Team 14969 — Vortex**. Write and test TeleOp and Autonomous OpModes on your laptop — no robot needed.

---

## Setup (do this once)

### 1. Install Git

**macOS** — open Terminal and run `git --version`. If it's not installed, macOS will prompt you automatically.

**Windows** — download from [git-scm.com](https://git-scm.com/download/win), install with default options.

### 2. Get Java

The easiest option: install **Android Studio** from [developer.android.com/studio](https://developer.android.com/studio). It includes Java and is the recommended editor for this project. If you already have Android Studio, you're good.

Alternatively, install JDK 17 from [adoptium.net](https://adoptium.net/temurin/releases/?version=17).

### 3. Fork & clone

1. Click **Fork** at the top of this page
2. Copy the URL of your fork
3. In Terminal:

```bash
git clone https://github.com/YOUR_USERNAME/VortexRobotSimulator.git
cd VortexRobotSimulator
git remote add upstream https://github.com/FTC14969Vortex/VortexRobotSimulator.git
```

### 4. Open in Android Studio

**File → Open** → select the `VortexRobotSimulator` folder. Wait for Gradle to sync (first time downloads Java 17 automatically — needs internet).

Then select **Run Simulator** from the run dropdown and click ▶.

### 5. Run from Terminal (alternative)

**macOS with Android Studio installed:**
```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jre/Contents/Home" ./run.sh
```

**Windows:**
```bat
gradlew.bat :TeamCode:run
```

> First run takes a minute to download Gradle and Java 17. After that it's fast.

---

## Using the Simulator

1. Pick an OpMode from the dropdown
2. Click **INIT**, then **START**
3. Use the keyboard to drive

### Keyboard Controls

| Key | Action |
|-----|--------|
| ↑ / ↓ | Forward / Back |
| ← / → | Strafe Left / Right |
| W / A / S / D | Same as arrows |
| r | Rotate CCW |
| R (Shift+R) | Rotate CW |
| b | Left bumper |
| B (Shift+B) | Right bumper |
| U / O | Left / Right trigger |
| Space | START (releases `waitForStart()`) |

---

## Writing an OpMode

Create a `.java` file in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/OpModes/`. It will appear in the dropdown automatically.

```java
import com.vortex.simulator.api.LinearOpMode;
import com.vortex.simulator.api.annotations.TeleOp;

@TeleOp(name = "My Mode", group = "TeleOp")
public class MyMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Ready!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("stick", gamepad1.left_stick_y);
            telemetry.update();
            sleep(5);
        }
    }
}
```

Use `@Autonomous` instead of `@TeleOp` for autonomous modes.

---

## Project Layout

```
TeamCode/          ← your code goes here
  Helper/          ← Chassis, Robot, LimelightHelper, Constants
  OpModes/         ← TeleOpMode, AutoBlue (your modes go here too)

simulator/         ← simulator engine, don't modify
```

---

## Troubleshooting

**OpMode doesn't appear in the dropdown**
Check that your class has a `@TeleOp` or `@Autonomous` annotation and is in the `OpModes/` folder.

**Build fails on first run**
Make sure you have an internet connection — Gradle needs to download Java 17.

**`./run.sh: permission denied`**
```bash
chmod +x run.sh && ./run.sh
```

---

See [CONTRIBUTING.md](CONTRIBUTING.md) to learn how to submit your work using GitHub Flow.
