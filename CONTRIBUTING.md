# Contributing — GitHub Flow

This is how our team shares and reviews code. Follow these steps every time you work on something.

---

## The Short Version

1. Create a branch
2. Write code, commit often
3. Push and open a Pull Request
4. Get a teammate to review it
5. Merge after approval

---

## Step by Step

### 1. Get the latest code

```bash
git checkout main
git pull origin main
```

### 2. Create a branch

Name it after what you're building:

```bash
git checkout -b feature/my-teleop
```

| Prefix | When to use |
|--------|-------------|
| `feature/` | New OpMode or feature |
| `fix/` | Bug fix |
| `tune/` | Adjusting constants or PID |

### 3. Write your OpMode and test it

Add your file to `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/OpModes/` and run the simulator to test it.

### 4. Commit your changes

```bash
git add TeamCode/src/main/java/org/firstinspires/ftc/teamcode/OpModes/MyMode.java
git commit -m "Add autonomous routine for blue alliance"
```

Write commit messages that describe **what changed and why**, not just "updated stuff".

Good:
- `Add field-centric TeleOp`
- `Fix heading drift during autonomous turn`

Bad:
- `stuff`
- `asdfgh`
- `it works now`

### 5. Push and open a Pull Request

```bash
git push origin feature/my-teleop
```

Then go to GitHub, click **"Compare & pull request"**, and fill in:
- What does this do?
- How did you test it?

### 6. Review and merge

- Assign a teammate as reviewer
- Address any feedback by pushing more commits to the same branch
- Don't merge your own PR — wait for approval

### 7. Clean up

```bash
git checkout main
git pull origin main
git branch -d feature/my-teleop
```

---

## Code Tips

- **Use `Constants.java`** for motor names and tuning numbers — never hardcode strings like `"frontLeft"` directly in your OpMode
- **Call `telemetry.update()` every loop** — it's how you debug
- **Add `sleep(5)`** at the end of every `while (opModeIsActive())` loop to avoid freezing the simulator
- **Test in the simulator** before opening a PR

---

## Getting Help

- Ask in `#robot-code` on Discord
- Tag `@mentor` for questions
- Open a [GitHub Issue](https://github.com/FTC14969Vortex/VortexRobotSimulator/issues) if you find a bug in the simulator
