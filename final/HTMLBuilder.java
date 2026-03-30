import java.util.*;

// Builds the full HTML page for the monitoring dashboard
// Dark-themed, card-based, auto-refreshes every 3 seconds
public class HTMLBuilder {

    public static String build(List<LogEvent> logs, Set<String> failed,
                               Set<String> allServices, long totalLogs,
                               double logsPerSec, long uptime,
                               Map<String, Integer> counts) {

        StringBuilder sb = new StringBuilder();
        sb.append(head());
        sb.append("<body>\n");
        sb.append(header());
        sb.append("<main>\n");
        sb.append(statsBar(totalLogs, logsPerSec, uptime, allServices.size(), failed.size()));
        sb.append("<div class='grid'>\n");
        sb.append(serviceHealthCard(allServices, failed, counts));
        sb.append(alertsCard(logs, failed));
        sb.append("</div>\n");
        sb.append(liveLogsCard(logs));
        sb.append("</main>\n");
        sb.append("</body></html>");
        return sb.toString();
    }

    private static String head() {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="refresh" content="3">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Order Pipeline Health Dashboard</title>
<style>
  @import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&family=Syne:wght@400;700;800&display=swap');
  :root {
    --bg:      #050d1a;
    --surface: #0b1828;
    --card:    #0f2035;
    --border:  #1a3a5c;
    --accent:  #00d4ff;
    --green:   #00e676;
    --red:     #ff3d57;
    --yellow:  #ffd740;
    --text:    #c8ddef;
    --muted:   #4a7090;
    --mono:    'JetBrains Mono', monospace;
    --sans:    'Syne', sans-serif;
  }
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body { background: var(--bg); color: var(--text); font-family: var(--sans); min-height:100vh; }
  header { background: var(--surface); border-bottom: 1px solid var(--border);
           padding: 18px 32px; display:flex; align-items:center; gap:16px; }
  header h1 { font-size: 1.35rem; font-weight:800; color:#fff; }
  .tag { font-size:.65rem; font-family:var(--mono); background:rgba(0,212,255,.12);
         color:var(--accent); border:1px solid rgba(0,212,255,.25); border-radius:4px; padding:3px 8px; }
  main { padding: 24px 32px; max-width:1400px; }
  .stats-bar { display:flex; gap:16px; margin-bottom:24px; flex-wrap:wrap; }
  .stat { background:var(--card); border:1px solid var(--border); border-radius:10px;
          padding:14px 22px; flex:1; min-width:130px; }
  .stat .val { font-size:1.7rem; font-weight:800; color:#fff; font-family:var(--mono); }
  .stat .lbl { font-size:.7rem; color:var(--muted); margin-top:2px; text-transform:uppercase; }
  .grid { display:grid; grid-template-columns:1fr 1fr; gap:20px; margin-bottom:20px; }
  .card { background:var(--card); border:1px solid var(--border); border-radius:12px; padding:20px; }
  .card h2 { font-size:.8rem; text-transform:uppercase; color:var(--muted);
             margin-bottom:16px; }
  .service-row { display:flex; align-items:center; justify-content:space-between;
                 padding:10px 14px; border-radius:8px; margin-bottom:8px;
                 background:rgba(255,255,255,.03); }
  .service-row .name { font-weight:700; color:#fff; }
  .service-row .count { font-family:var(--mono); color:var(--muted); }
  .badge { font-size:.65rem; font-family:var(--mono); padding:3px 10px; border-radius:20px; font-weight:700; }
  .badge.ok    { background:rgba(0,230,118,.15); color:var(--green); }
  .badge.fail  { background:rgba(255,61,87,.15);  color:var(--red); }
  .alert-item { padding:10px 14px; border-radius:8px; margin-bottom:8px;
                font-family:var(--mono); border-left:3px solid; }
  .alert-item.error  { background:rgba(255,61,87,.08);  border-color:var(--red); }
  .alert-item.failed { background:rgba(255,215,64,.08); border-color:var(--yellow); }
  .log-table-wrap { overflow-y:auto; max-height:420px; }
  table { width:100%; border-collapse:collapse; font-family:var(--mono); }
  thead th { background:var(--surface); padding:10px 12px;
             text-align:left; color:var(--muted); border-bottom:1px solid var(--border); }
  td { padding:9px 12px; }
  .time { color:var(--muted); }
  .svc  { color:var(--accent); font-weight:700; }
  .lvl-INFO  { color:var(--green); }
  .lvl-ERROR { color:var(--red); font-weight:700; }
</style>
</head>
""";
    }

    private static String header() {
        return """
<header>
  <h1>Order Pipeline Health Dashboard</h1>
  <span class="tag">LIVE</span>
</header>
""";
    }

    private static String statsBar(long total, double lps, long uptime, int svcs, int failed) {
        return "<div class='stats-bar'>" +
               stat(String.valueOf(total), "Total Logs") +
               stat(String.format("%.1f", lps), "Logs/sec") +
               stat(String.valueOf(svcs), "Services") +
               stat(String.valueOf(failed), "Failed") +
               stat(uptime + "s", "Uptime") +
               "</div>\n";
    }

    private static String stat(String val, String lbl) {
        return "<div class='stat'><div class='val'>" + val +
               "</div><div class='lbl'>" + lbl + "</div></div>\n";
    }

    private static String serviceHealthCard(Set<String> all, Set<String> failed,
                                             Map<String, Integer> counts) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='card'><h2>Service Health</h2>\n");

        if (all.isEmpty()) {
            sb.append("<p>No services connected yet.</p>");
        }

        for (String svc : all) {
            boolean down = failed.contains(svc);
            int cnt = counts.getOrDefault(svc, 0);

            sb.append("<div class='service-row'>")
              .append("<span class='name'>").append(escHtml(svc)).append("</span>")
              .append("<span class='count'>").append(cnt).append(" logs</span>")
              .append("<span class='badge ").append(down ? "fail" : "ok").append("'>")
              .append(down ? "FAILED" : "HEALTHY")
              .append("</span></div>\n");
        }

        sb.append("</div>\n");
        return sb.toString();
    }

    private static String alertsCard(List<LogEvent> logs, Set<String> failed) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='card'><h2>Recent Alerts</h2>\n");

        int alertCount = 0;

        for (String svc : failed) {
            sb.append("<div class='alert-item failed'>")
              .append("<b>").append(escHtml(svc)).append("</b> - service not responding</div>\n");
            if (++alertCount >= 5) break;
        }

        for (LogEvent e : logs) {
            if ("ERROR".equals(e.level) && alertCount < 10) {
                sb.append("<div class='alert-item error'>[")
                  .append(e.formattedTime())
                  .append("] <b>").append(escHtml(e.service)).append("</b> - ")
                  .append(escHtml(e.message)).append("</div>\n");
                alertCount++;
            }
        }

        if (alertCount == 0) {
            sb.append("<p>No active alerts</p>");
        }

        sb.append("</div>\n");
        return sb.toString();
    }

    private static String liveLogsCard(List<LogEvent> logs) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='card'><h2>Live Logs</h2>\n");
        sb.append("<div class='log-table-wrap'><table>\n");
        sb.append("<thead><tr><th>Time</th><th>Service</th><th>Level</th><th>Message</th></tr></thead>\n");
        sb.append("<tbody>\n");

        if (logs.isEmpty()) {
            sb.append("<tr><td colspan='4'>Waiting for logs...</td></tr>");
        }

        for (LogEvent e : logs) {
            sb.append("<tr>")
              .append("<td class='time'>").append(e.formattedTime()).append("</td>")
              .append("<td class='svc'>").append(escHtml(e.service)).append("</td>")
              .append("<td class='lvl-").append(e.level).append("'>").append(e.level).append("</td>")
              .append("<td>").append(escHtml(e.message)).append("</td>")
              .append("</tr>\n");
        }

        sb.append("</tbody></table></div></div>\n");
        return sb.toString();
    }

    private static String escHtml(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}