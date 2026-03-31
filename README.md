# OffTempo

**Statistical timing side-channel analysis for Burp Suite.**

OffTempo is a Burp Suite extension that captures HTTP response timing data from Intruder attacks and performs statistical analysis to detect timing-based side channels. It compares two pools of requests and tells you whether the observed latency differences are meaningful or just noise.

Useful for detecting:
- Resource enumeration via response time differentials
- Timing-based blind SQL injection
- Password / authentication timing attacks
- Any scenario where server-side branching leaks through response latency

The primary metric is **AUC (Area Under the Curve) via Mann–Whitney U** — intuitively, the probability that a random observation from Pool A exceeds one from Pool B. Additional statistics (Cohen's *d*, SNR, descriptive stats) are provided for deeper analysis.

## Installation

### Prebuilt JAR (recommended)

1. Download the latest `offtempo.jar` from the [Releases](https://github.com/anvilventures/offtempo/releases) page
2. In Burp Suite, go to **Extensions → Installed → Add**
3. Set **Extension type: Java** and load the JAR

### Build from source

Requires Java 17+ and Gradle.

```bash
git clone https://github.com/anvilventures/offtempo.git
cd offtempo
./gradlew bigJar
```

The fat JAR is generated under `build/libs/`. Load it into Burp Suite as above.

## Usage

1. **Enable capture** — Toggle timing capture on in the OffTempo tab
2. **Fill Pool A** — Set up an Intruder attack for your first class of requests (e.g. a known *existing* resource) and run it. Timing data flows into Pool A automatically
3. **Switch to Pool B** — Select Pool B in OffTempo, then run a second Intruder attack for your other class of requests (e.g. a known *non-existing* resource)
4. **Run analysis** — Click **Run**. OffTempo computes the AUC score, plots both distributions, and outputs statistical metrics

The two pools don't need to contain identical requests. For enumeration testing it makes sense to send identical requests per pool to average out network jitter. For blind SQLi, one pool can be your baseline while the other contains injection payloads.

### Interpreting results

| AUC | Interpretation |
|-----|----------------|
| ~0.5 | No distinguishable difference — pools overlap completely |
| 0.6–0.7 | Weak signal — may warrant further investigation |
| 0.7–0.8 | Moderate signal — likely exploitable with enough samples |
| >0.8 | Strong signal — clear timing side channel |

## Related work

- [Timeinator](https://github.com/FSecureLABS/timeinator) by F-Secure — A Burp Suite extension with a similar goal. Timeinator reimplements Intruder "sniper" mode within its own tab and reports basic descriptive statistics (mean, median, std dev). OffTempo differs by working with the full Intruder feature set and providing AUC-based distinguishability scoring for faster triage.

## License

See [LICENSE](LICENSE).
