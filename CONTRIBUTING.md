# Contributing to VortexRobotSimulator

Welcome to Team 14969! This guide walks you through the **GitHub Flow** workflow we use for all robot code.

---

## GitHub Flow Overview

```
main (protected)
  └── feature/your-branch
        └── Pull Request → review → merge
```

1. **Fork** the repo (first time only)
2. **Create a branch** for your feature
3. **Commit** your changes with clear messages
4. **Push** your branch and **open a PR**
5. **Request a review** from a teammate
6. **Merge** after approval

---

## Step-by-Step

### 1. Fork the repository

Click **Fork** on GitHub. This creates your own copy at `github.com/YOUR_USERNAME/VortexRobotSimulator`.

### 2. Clone your fork

```bash
git clone https://github.com/YOUR_USERNAME/VortexRobotSimulator.git
cd VortexRobotSimulator
```

### 3. Add the upstream remote (one-time)

```bash
git remote add upstream https://github.com/VortexDecode/VortexRobotSimulator.git
```

### 4. Create a branch

Name your branch after what you're working on:

```bash
git checkout -b feature/field-centric-teleop
# or
git checkout -b fix/auto-heading-drift
```

### 5. Write your OpMode

Add your file to `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/OpModes/`.

Test it:
```bash
./run.sh
```

### 6. Commit your changes

Write clear, present-tense commit messages:

```bash
git add TeamCode/src/main/java/org/firstinspires/ftc/teamcode/OpModes/MyMode.java
git commit -m "Add field-centric TeleOp with heading lock"
```

**Good commit messages:**
- `Add AprilTag alignment to AutoRed`
- `Fix heading drift in autonomous turn`
- `Tune DRIVE_SPEED constant for smoother auto`

**Bad commit messages:**
- `stuff`
- `fixed it`
- `WIP`

### 7. Push your branch

```bash
git push origin feature/field-centric-teleop
```

### 8. Open a Pull Request

1. Go to your fork on GitHub
2. Click **"Compare & pull request"**
3. Write a clear description:
   - What does this OpMode/change do?
   - How did you test it in the simulator?
   - Any known issues?

### 9. Code Review

- Assign at least one teammate as reviewer
- Respond to feedback — push new commits to the same branch
- Don't merge your own PR without a review

### 10. Merge and clean up

After approval:
```bash
# After the PR is merged on GitHub:
git checkout main
git pull upstream main
git branch -d feature/field-centric-teleop
```

---

## Branch Naming Conventions

| Prefix | Use for |
|--------|---------|
| `feature/` | New OpModes or capabilities |
| `fix/` | Bug fixes |
| `tune/` | Constant/PID tuning |
| `docs/` | Documentation only |
| `refactor/` | Code cleanup (no behavior change) |

---

## Code Standards

- **One OpMode per file** — keep files small and focused
- **Use Constants.java** for all hardware names and tuning values — never hardcode strings
- **Telemetry in every loop** — makes debugging much easier
- **Comment the `why`**, not the `what`:
  ```java
  // Reverse because motors are mounted facing backward on our chassis
  frontRight.setDirection(Direction.REVERSE);
  ```
- **Test in the simulator before opening a PR**

---

## Simulator Tips

- Use **AutoBlue** as a template for new autonomous modes
- Use **AprilTag Follower** to understand Limelight integration
- The **CameraPanel** shows what the Limelight "sees" — watch it while testing tag alignment
- The **TelemetryPanel** mirrors what you'd see on the Driver Hub

---

## Getting Help

- Post in `#robot-code` on the team Discord
- Tag `@mentor` for design questions
- Open a GitHub Issue for bugs in the simulator itself

Happy coding! 🤖
