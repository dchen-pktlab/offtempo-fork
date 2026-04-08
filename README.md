# OffTempo ⌛

**Statistical timing side-channel analysis for Burp Suite.**

<img width="1370" height="1022" alt="Screenshot 2026-04-07 165132" src="https://github.com/user-attachments/assets/9bfa6816-02b3-4bf7-928e-41525e9dc858" />

OffTempo is a Burp Suite extension that captures HTTP response timing data from Intruder attacks and performs statistical analysis to detect timing-based side channels. It compares two pools of requests and tells you whether the observed latency differences are meaningful or just noise.

Useful for detecting:
- Resource enumeration via response time differentials
- Timing-based blind SQL injection
- Password / authentication timing attacks
- Any scenario where server-side branching leaks through response latency

The primary metric is **AUC (Area Under the Curve) via Mann–Whitney U** - intuitively, the probability that a random observation from a set of requests exceeds one from another set. Additional statistics (Cohen's *d*, p-value, standard deviation, p95/p99) are provided for deeper analysis.

## Installation

### Prebuilt JAR (recommended)

1. Download the latest `offtempo.jar` from the [Releases](https://github.com/anvilventures/offtempo/releases) page
2. In Burp Suite, go to **Extensions → Installed → Add**
3. Set **Extension type: Java** and load the JAR

### Build from source

Requires Java 17+ and Gradle.

```
git clone https://github.com/anvilventures/offtempo.git
cd offtempo
./gradlew bigJar
```

The JAR is generated under `build/libs/`. Load it into Burp Suite as above.

## Usage


1.  **Enable capture**: Toggle timing capture on in the OffTempo tab
<img width="1367" height="300" alt="Screenshot 2026-04-07 165251" src="https://github.com/user-attachments/assets/f396c665-37b3-4296-8b99-399c4a17fedc" />

2.   **Fill Pool A**: Set up an Intruder attack for your first class of requests (e.g. a known *existing* resource) and run it. Timing data flows into Pool A automatically. Aim for at least 30–50 requests per pool to reduce the impact of network jitter and get statistically reliable results.
<img width="1372" height="307" alt="Screenshot 2026-04-07 165222" src="https://github.com/user-attachments/assets/e86e8422-92f9-4dc1-8410-1274e97a83ca" />

3.   **Switch to Pool B**: Select Pool B in OffTempo, then run a second Intruder attack for your other class of requests (e.g. a known *non-existing* resource).
<img width="1373" height="305" alt="Screenshot 2026-04-07 165202" src="https://github.com/user-attachments/assets/38e0e905-03c5-4a9c-bc0d-0514271c6594" />

4.  **Run analysis**: Click **Run**. OffTempo computes the AUC score, plots both distributions, and outputs statistical metrics. Use **Export CSV** to save raw timing data for offline analysis or to share with a colleague.
<img width="1374" height="651" alt="Screenshot 2026-04-07 165149" src="https://github.com/user-attachments/assets/23c59882-310d-4c64-a129-da4f0755d08f" />


The two pools don't need to contain identical requests. For blind SQLi, one pool can be your baseline while the other contains injection payloads. The request structure can differ as long as the timing difference is what you're measuring.
