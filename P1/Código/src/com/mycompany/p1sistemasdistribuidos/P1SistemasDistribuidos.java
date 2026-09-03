
/**
 * Práctica 1: Barbero (Productor - Consumidor)
 * Sistemas Distribuidos | Desarrollado por: Ricardo Carmona Martínez
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class P1SistemasDistribuidos extends JFrame {
    private static final int TOTAL_SILLAS = 5;
    private final Barberia barberia = new Barberia(TOTAL_SILLAS);
    private int contadorClientes = 1, atendidosTotales = 0, rechazadosTotales = 0;
    private volatile boolean autoActivo = false;

    // Componentes UI
    private final JLabel lblStatus = lbl("ESTADO: DURMIENDO (WAIT)", Font.BOLD, 14, new Color(245, 158, 11), 0);
    private final JLabel lblCliente = lbl("En silla de corte: Ninguno", Font.PLAIN, 13, new Color(243, 244, 246), 0);
    private final JLabel lblContador = lbl("0 / 5 ocupadas", Font.BOLD, 13, new Color(59, 130, 246), 4);
    private final JLabel lblAtendidos = lbl("Atendidos: 0", Font.BOLD, 13, new Color(34, 197, 94), 4);
    private final JLabel lblRechazados = lbl("Rechazados (Overflow): 0", Font.BOLD, 13, new Color(239, 68, 68), 4);
    private final JPanel pnlSillas = new JPanel(new GridLayout(1, TOTAL_SILLAS, 10, 0));
    private final JTextArea txtLogs = new JTextArea();
    private JButton btnAuto;

    private static final Color BG_MAIN = new Color(18, 20, 29), BG_CARD = new Color(28, 32, 47);
    private static final Color ACC_BLUE = new Color(59, 130, 246), ACC_RED = new Color(239, 68, 68);
    private static final Color TXT_MAIN = new Color(243, 244, 246), TXT_MUTED = new Color(156, 163, 175);

    public P1SistemasDistribuidos() {
        super("Sistemas Distribuidos - Práctica 1: Barbero Dormilón - Ricardo Carmona Martínez");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 640);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(BG_MAIN);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        // Header con títulos y métricas
        JPanel header = card(new BorderLayout(15, 0)), pnlTits = new JPanel(new GridLayout(2, 1, 0, 2)),
                pnlMets = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlTits.setOpaque(false);
        pnlMets.setOpaque(false);
        pnlTits.add(lbl("SIMULADOR DE CONCURRENCIA: PRODUCTOR - CONSUMIDOR", Font.BOLD, 16, TXT_MAIN, 2));
        pnlTits.add(lbl("Sección Crítica, Buffer Acotado y Exclusión Mutua | Ricardo Carmona Martínez", Font.PLAIN, 12,
                TXT_MUTED, 2));
        pnlMets.add(lblAtendidos);
        pnlMets.add(lblRechazados);
        header.add(pnlTits, BorderLayout.WEST);
        header.add(pnlMets, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Escenario Visual: Barbero + Buffer
        JPanel visual = new JPanel(new GridLayout(1, 2, 12, 0)), cardB = card(new BorderLayout(8, 8)),
                cardS = card(new BorderLayout(8, 8)), infoB = new JPanel(new GridLayout(3, 1, 4, 4)),
                headS = new JPanel(new BorderLayout());
        visual.setOpaque(false);
        infoB.setOpaque(false);
        headS.setOpaque(false);
        infoB.add(lblStatus);
        infoB.add(lblCliente);
        infoB.add(lbl("Sin consumo de CPU (Espera Pasiva)", Font.ITALIC, 11, TXT_MUTED, 0));
        cardB.add(lbl("CONSUMIDOR (BARBERO)", Font.BOLD, 13, TXT_MUTED, 0), BorderLayout.NORTH);
        cardB.add(infoB, BorderLayout.CENTER);
        headS.add(lbl("BUFFER ACOTADO (SALA DE ESPERA)", Font.BOLD, 13, TXT_MUTED, 2), BorderLayout.WEST);
        headS.add(lblContador, BorderLayout.EAST);
        pnlSillas.setOpaque(false);
        renderizarSillas(Collections.emptyList());
        cardS.add(headS, BorderLayout.NORTH);
        cardS.add(pnlSillas, BorderLayout.CENTER);
        visual.add(cardB);
        visual.add(cardS);

        // Consola de eventos y SplitPane
        JPanel pnlLogs = card(new BorderLayout(5, 5));
        pnlLogs.add(lbl("CONSOLA DE EVENTOS Y MONITOR DE HILOS", Font.BOLD, 11, TXT_MUTED, 2), BorderLayout.NORTH);
        txtLogs.setEditable(false);
        txtLogs.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLogs.setBackground(new Color(13, 15, 22));
        txtLogs.setForeground(new Color(229, 231, 235));
        txtLogs.setBorder(new EmptyBorder(6, 6, 6, 6));
        JScrollPane scroll = new JScrollPane(txtLogs);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(40, 46, 62)));
        pnlLogs.add(scroll, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, visual, pnlLogs);
        split.setDividerLocation(260);
        split.setDividerSize(4);
        split.setOpaque(false);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        // Barra de botones inferiores
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pnlBtns.setOpaque(false);
        pnlBtns.add(btn(" + Arribar 1 Cliente ", ACC_BLUE, e -> despacharCliente("Cli-" + contadorClientes++)));
        pnlBtns.add(btnAuto = btn(" Iniciar Tráfico Automático ", new Color(107, 114, 128), e -> alternarAuto()));
        pnlBtns.add(btn(" Limpiar Consola ", new Color(55, 65, 81), e -> txtLogs.setText("")));
        root.add(pnlBtns, BorderLayout.SOUTH);

        iniciarHiloBarbero();
    }

    private void renderizarSillas(List<String> cli) {
        pnlSillas.removeAll();
        for (int i = 0; i < TOTAL_SILLAS; i++) {
            boolean oc = i < cli.size();
            JPanel s = new JPanel(new BorderLayout());
            s.setBackground(oc ? new Color(30, 58, 102) : new Color(22, 25, 36));
            s.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(oc ? ACC_BLUE : new Color(45, 52, 70), 1),
                    new EmptyBorder(6, 4, 6, 4)));
            s.add(lbl("Silla #" + (i + 1), Font.PLAIN, 10, TXT_MUTED, 0), BorderLayout.NORTH);
            s.add(lbl(oc ? "[ OCUPADA ]" : "[ LIBRE ]", Font.BOLD, 11, oc ? ACC_BLUE : TXT_MUTED, 0),
                    BorderLayout.CENTER);
            s.add(lbl(oc ? cli.get(i) : "Vacía", Font.BOLD, 11, oc ? TXT_MAIN : TXT_MUTED, 0), BorderLayout.SOUTH);
            pnlSillas.add(s);
        }
        pnlSillas.revalidate();
        pnlSillas.repaint();
    }

    private void log(String tag, String msg) {
        SwingUtilities.invokeLater(() -> {
            txtLogs.append(String.format("[%tT] [%s] %s\n", System.currentTimeMillis(), tag, msg));
            txtLogs.setCaretPosition(txtLogs.getDocument().getLength());
        });
    }

    private void actualizar(boolean oc, String cli, List<String> esp) {
        SwingUtilities.invokeLater(() -> {
            lblStatus.setText(oc ? "ESTADO: ATENDIENDO (EN CORTE)" : "ESTADO: DURMIENDO (WAIT)");
            lblStatus.setForeground(oc ? new Color(34, 197, 94) : new Color(245, 158, 11));
            lblCliente.setText(oc ? "En silla de corte: " + cli : "En silla de corte: Ninguno");
            lblContador.setText(esp.size() + " / " + TOTAL_SILLAS + " ocupadas");
            lblAtendidos.setText("Atendidos: " + atendidosTotales);
            lblRechazados.setText("Rechazados (Overflow): " + rechazadosTotales);
            renderizarSillas(esp);
        });
    }

    private void iniciarHiloBarbero() {
        Thread t = new Thread(() -> {
            while (true)
                try {
                    actualizar(false, null, barberia.getSnapshot());
                    log("BARBERO", "Buffer vacío. Barbero duerme en wait()...");
                    String c = barberia.atender();
                    actualizar(true, c, barberia.getSnapshot());
                    log("BARBERO", "Cortando cabello a: " + c);
                    Thread.sleep(2000);
                    atendidosTotales++;
                    log("BARBERO", "Terminó de atender a " + c);
                } catch (InterruptedException e) {
                    break;
                }
        });
        t.setDaemon(true);
        t.start();
    }

    private void despacharCliente(String cli) {
        new Thread(() -> {
            if (barberia.llegar(cli))
                log("PRODUCTOR", cli + " tomó una silla de espera.");
            else {
                rechazadosTotales++;
                log("OVERFLOW", cli + " se retira: sala llena.");
            }
            actualizar(barberia.isOcupado(), barberia.getClienteActual(), barberia.getSnapshot());
        }).start();
    }

    private void alternarAuto() {
        autoActivo = !autoActivo;
        btnAuto.setText(autoActivo ? " Detener Tráfico Automático " : " Iniciar Tráfico Automático ");
        btnAuto.setBackground(autoActivo ? ACC_RED : new Color(107, 114, 128));
        if (autoActivo)
            new Thread(() -> {
                while (autoActivo)
                    try {
                        despacharCliente("CliAuto-" + contadorClientes++);
                        Thread.sleep((long) (700 + Math.random() * 1500));
                    } catch (InterruptedException e) {
                        break;
                    }
            }).start();
    }

    private JPanel card(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setBackground(BG_CARD);
        p.setBorder(new EmptyBorder(10, 14, 10, 14));
        return p;
    }

    private JLabel lbl(String t, int s, int sz, Color c, int al) {
        JLabel l = new JLabel(t, al);
        l.setFont(new Font("Segoe UI", s, sz));
        l.setForeground(c);
        return l;
    }

    private JButton btn(String t, Color bg, ActionListener a) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 14, 8, 14));
        b.addActionListener(a);
        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new P1SistemasDistribuidos().setVisible(true));
    }

    // Monitor de Sincronización (Buffer Acotado con synchronized, wait y notifyAll)
    static class Barberia {
        private final int max;
        private final Queue<String> sillas = new LinkedList<>();
        private boolean ocupado = false;
        private String actual = null;

        public Barberia(int max) {
            this.max = max;
        }

        public synchronized boolean llegar(String c) {
            if (sillas.size() < max) {
                sillas.add(c);
                notifyAll();
                return true;
            }
            return false;
        }

        public synchronized String atender() throws InterruptedException {
            while (sillas.isEmpty()) {
                ocupado = false;
                actual = null;
                wait();
            }
            ocupado = true;
            return actual = sillas.poll();
        }

        public synchronized List<String> getSnapshot() {
            return new ArrayList<>(sillas);
        }

        public synchronized boolean isOcupado() {
            return ocupado;
        }

        public synchronized String getClienteActual() {
            return actual;
        }
    }
}
