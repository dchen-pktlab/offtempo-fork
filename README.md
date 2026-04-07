# OffTempo ⌛

**Statistical timing side-channel analysis for Burp Suite.**


<img width="1312" height="1120" alt="Pasted image 20260217170417" src="https://github.com/user-attachments/assets/194fc1d3-b403-49a6-9885-13e159291959" />

OffTempo is a Burp Suite extension that captures HTTP response timing data from Intruder attacks and performs statistical analysis to detect timing-based side channels. It compares two pools of requests and tells you whether the observed latency differences are meaningful or just noise.

Useful for detecting:
- Resource enumeration via response time differentials
- Timing-based blind SQL injection
- Password / authentication timing attacks
- Any scenario where server-side branching leaks through response latency

The primary metric is **AUC (Area Under the Curve) via Mann–Whitney U** - intuitively, the probability that a random observation from Pool A exceeds one from Pool B. Additional statistics (Cohen's *d*, p-value, standard deviation, p95/p99) are provided for deeper analysis.

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
  <img width="1313" height="341" alt="Pasted image 20260217165855" src="https://github.com/user-attachments/assets/c155f887-0523-40e1-8ead-d8a4604f44ab" />

2.   **Fill Pool A**: Set up an Intruder attack for your first class of requests (e.g. a known *existing* resource) and run it. Timing data flows into Pool A automatically. Aim for at least 30–50 requests per pool to reduce the impact of network jitter and get statistically reliable results.
<img width="1302" height="359" alt="Pasted image 20260217173558" src="https://github.com/user-attachments/assets/93cbdee8-ea37-4f61-8bcb-5b6bb3856ca9" />

3.   **Switch to Pool B**: Select Pool B in OffTempo, then run a second Intruder attack for your other class of requests (e.g. a known *non-existing* resource). For enumeration testing, use the same request template as Pool A to isolate timing differences from content differences.
<img width="1300" height="427" alt="Pasted image 20260217173733" src="https://github.com/user-attachments/assets/48dce55a-cb6a-4066-88c9-54d8f8e25a81" />

4.  **Run analysis**: Click **Run**. OffTempo computes the AUC score, plots both distributions, and outputs statistical metrics. Use **Export CSV** to save raw timing data for offline analysis or to share with a colleague.
<img width="1314" height="873" alt="Pasted image 20260217173915" src="https://github.com/user-attachments/assets/e613c78b-378e-418b-9699-873ccd932890" />

The two pools don't need to contain identical requests. For blind SQLi, one pool can be your baseline while the other contains injection payloads. The request structure can differ as long as the timing difference is what you're measuring.
