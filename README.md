# Guardian

A complete and configurable **report system** for Minecraft servers (Spigot/Paper 1.21.x).  
Guardian allows players to report others through a GUI and provides admins with powerful management tools.

---

## ✨ Features
- **Player Reports**:  
  `/report <player>` opens a clean GUI with reasons (cheating, insults, bug abuse, griefing, spam, other).
- **Beautiful GUI**:  
  Compact design, filled with gray glass, themed icons for each reason.
- **Cooldown System**:  
  Prevent spam by limiting reports to once every 2 hours (configurable).
- **Persistent Storage**:  
  All reports saved in YAML with unique IDs, reporter, reported, reason, timestamp, and status.
- **Admin Tools**:
  - `/guardian list` → Shows all open reports.
  - `/guardian complete <id>` → Closes a report and notifies the reporter.
  - `/guardian history <player>` → Displays the full report history of a player.
  - `/guardian reload` → Reloads the configuration.
- **Notifications**:  
  OPs are notified on join if there are open reports, with clickable messages to view them.
- **Configurable Messages**:  
  All prefixes, messages, cooldowns, and GUI items are fully editable in `config.yml`.

---

## ⚙️ Configuration
The plugin generates a `config.yml` with:
- Customizable prefix (default: `&f[&6&lɢᴜᴀʀᴅɪᴀɴ&f]`)
- Report cooldown settings
- Messages and GUI texts
- Reasons and their icons

---

## 📂 Commands
| Command                     | Description                             | Permission            |
|-----------------------------|-----------------------------------------|-----------------------|
| `/report <player>`          | Report a player via GUI                 | `guardian.use`        |
| `/guardian list`            | List all open reports                   | `guardian.admin`      |
| `/guardian complete <id>`   | Close a report                          | `guardian.admin`      |
| `/guardian history <player>`| Show full report history of a player    | `guardian.admin`      |
| `/guardian reload`          | Reload the configuration                | `guardian.admin`      |
| `/guardian help`            | Show help page                          | `guardian.use`        |

---

## 🔧 Installation
1. Download the plugin JAR or build it with Maven.  
2. Place it into your server’s `plugins/` folder.  
3. Restart the server.  
4. Edit `config.yml` to fit your needs.  
5. Use `/guardian reload` to apply changes.

---

## 📝 License
This project is released under the MIT License.
