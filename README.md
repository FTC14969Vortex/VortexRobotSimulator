# VortexRobotSimulator

A desktop Java robot simulator for **FTC Team 14969 — Vortex**, designed to teach students GitHub Flow without needing physical hardware.

Students fork this repo, create branches, implement TeleOp and Autonomous OpModes, and open PRs — all in a realistic simulation of the INTO THE DEEP season field.

---

## Features

- **Full mecanum drive physics** — 50 Hz Euler integration with field-centric kinematics
- **Mock FTC SDK API** — `DcMotor`, `HardwareMap`, `LinearOpMode`, `Gamepad`, `Telemetry`, `GoBildaPinpointDriver`, `Limelight3A`
- **INTO THE DEEP AprilTag layout** — tags 11–16 at correct field positions
- **Simulated camera** — FOV ±30°, range 120", Gaussian noise, 20% miss probability
- **Overhead field view** — robot position, heading, and tag markers
- **Keyboard gamepad** — drive with WASD + arrow keys, no controller needed
- **Auto OpMode discovery** — any `@TeleOp` or `@Autonomous` class is picked up automatically

---

## Setup from Scratch

Follow these steps on a fresh machine. You only need to do this once.

### Step 1 — Install Git

**macOS:**
Open Terminal and run:
```bash
git --version
```
If Git is not installed, macOS will prompt you to install Xcode Command Line Tools. Click **Install** and wait for it to finish.

**Windows:**
Download and install from [git-scm.com](https://git-scm.com/download/win). Use all default options during setup.

Verify it worked:
```bash
git --version
# Should print something like: git version 2.x.x
```

---

### Step 2 — Install Java 17

The simulator requires Java 17. If you have **Android Studio** installed, you can use its bundled JDK instead of installing a separate one.

**Option A — Use Android Studio's JDK (recommended if you have Android Studio):**

No installation needed. Just note the path — you'll use it in Step 4:
- **macOS:** `/Applications/Android Studio.app/Contents/jre/Contents/Home`
- **Windows:** `C:\Program Files\Android\Android Studio\jre`

**Option B — Install a standalone JDK:**

Download JDK 17 from [adoptium.net](https://adoptium.net/temurin/releases/?version=17) and run the installer. Choose the `.pkg` (macOS) or `.msi` (Windows) installer.

Verify:
```bash
java -version
# Should print: openjdk version "17.x.x" ...
```

---

### Step 3 — Fork & Clone the Repository

1. Go to [github.com/VortexDecode/VortexRobotSimulator](https://github.com/VortexDecode/VortexRobotSimulator)
2. Click **Fork** (top right) to create your own copy
3. On your fork, click the green **Code** button and copy the URL
4. In Terminal, clone your fork:

```bash
git clone https://github.com/YOUR_USERNAME/VortexRobotSimulator.git
cd VortexRobotSimulator
```

Add the upstream remote so you can pull updates later:
```bash
git remote add upstream https://github.com/VortexDecode/VortexRobotSimulator.git
```

---

### Step 4 — Run the Simulator

The simulator downloads the rest of what it needs (Gradle 8.7, Java 17) automatically on first run. You need an internet connection.

**macOS — using Android Studio's JDK:**
```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jre/Contents/Home" ./run.sh
```

**macOS — using a standalone JDK 17:**
```bash
./run.sh
```

**Windows:**
```bat
gradlew.bat :TeamCode:run
```

> **First run takes 1–2 minutes** — Gradle downloads its own distribution and Java 17. Subsequent runs start in a few seconds.

The simulator window opens. Pick an OpMode, click **INIT**, then **START**.

---

### Step 5 — Verify It Works

1. Select **TeleOp** from the dropdown and click **INIT**
2. Click **START**
3. Press **W** to drive the robot forward on the field
4. The telemetry panel on the right should show changing X/Y values
5. Drive near a yellow tag marker — the Camera panel should show a reticle

If it works, you're all set. See [CONTRIBUTING.md](CONTRIBUTING.md) for the GitHub Flow workflow.

---

## Keyboard Controls

| Key | Action |
|-----|--------|
| W / S | Forward / Back |
| A / D | Strafe Left / Right |
| ← / → | Rotate Left / Right |
| ↑ / ↓ | Right stick Y |
| J / K / L / ; | Buttons a / b / x / y |
| Q / E | Left / Right bumper |
| U / O | Left / Right trigger |
| I | D-pad up |
| Space | Start (releases `waitForStart()`) |

---

## Adding an OpMode

1. Create a new `.java` file in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/OpModes/`
2. Annotate with `@TeleOp(name="My Mode")` or `@Autonomous(name="My Auto")`
3. Extend `LinearOpMode` and implement `runOpMode()`
4. Run the simulator — your mode appears in the dropdown automatically

```java
import com.vortex.simulator.api.LinearOpMode;
import com.vortex.simulator.api.annotations.TeleOp;

@TeleOp(name = "My First Mode", group = "Practice")
public class MyFirstMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Hello Vortex!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("left_stick_y", gamepad1.left_stick_y);
            telemetry.update();
        }
    }
}
```

---

## Project Structure

```
VortexRobotSimulator/
├── simulator/          ← Simulator engine (don't modify)
│   └── src/main/java/com/vortex/simulator/
│       ├── api/        ← Mock FTC SDK classes
│       ├── engine/     ← Physics, odometry, camera
│       ├── ui/         ← Swing UI panels
│       └── runner/     ← Main entry point, OpMode lifecycle
└── TeamCode/           ← YOUR CODE GOES HERE
    └── src/main/java/org/firstinspires/ftc/teamcode/
        ├── Helper/     ← Chassis, Robot, LimelightHelper, Constants
        └── OpModes/    ← TeleOpMode, AutoBlue, AprilTagFollower
```

---

## Simulator vs Real Robot

| Feature | Simulator | Real Robot |
|---------|-----------|------------|
| Import paths | `com.vortex.simulator.api.*` | `com.qualcomm.robotcore.*` |
| Physics | Euler integration (ideal) | Real hardware with friction |
| Camera | Gaussian noise model | Actual image processing |
| Latency | None | Real I2C/USB latency |
| Encoder ticks | Computed from motor powers | Physical encoder pulses |

When moving to the real robot, swap import paths and re-tune PID constants.

---

## Troubleshooting

**"No @TeleOp or @Autonomous classes found"**
Make sure your class is in `TeamCode/src/main/java/...` and has a `@TeleOp` or `@Autonomous` annotation.

**Robot doesn't respond to keyboard**
Click anywhere on the field panel to make sure the simulator window has focus, then try again.

**Build fails with "Java toolchain not found"**
You need internet access on first run so Gradle can download Java 17 automatically.

**`./run.sh: permission denied`**
```bash
chmod +x run.sh
./run.sh
```

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for the GitHub Flow workflow.
