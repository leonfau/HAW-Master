# Firewall deaktivieren

Auf den Laborrechnern für das Praktikum ist unter Linux (OpenSuSE 13.1) die Firewall aktiviert, unter Windows ist Firewall per Default deaktiviert. Diese verhindert, dass die Overlay-Knoten untereinander Verbindungen aufbauen können. Sie können die Firewall vorübergehend abschalten, gehen Sie dazu wie folgt vor:

1. Aktuelle Firewall-Regeln löschen:
    sudo /usr/sbin/iptables -F
    sudo /usr/sbin/iptables -X

2. Default Regeln anlegen:
    sudo /usr/sbin/iptables -P INPUT ACCEPT
    sudo /usr/sbin/iptables -P FORWARD ACCEPT
    sudo /usr/sbin/iptables -P OUTPUT ACCEPT

3. Neue Firewall-Regeln anzeigen
    sudo /usr/sbin/iptables -L

Es dürfen nur drei Regeln (INPUT, FORWARD, OUTPUT) existieren, die alle auf ACCEPT stehen. Beachten Sie, dass die Firewall nach einem Neustart/Reboot wieder aktiv ist! 