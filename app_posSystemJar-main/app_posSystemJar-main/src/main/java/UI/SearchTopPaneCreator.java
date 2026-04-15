package UI;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import java.awt.*;
import javax.swing.*;
import java.awt.event.FocusEvent;

public class SearchTopPaneCreator extends PanelRound {

    // Instance variable for the search text field
    private JTextField searchTextField;

    // Constructor to create a search panel with an icon and text field
    public SearchTopPaneCreator(String placeholderText, String iconPath) {
        // Set up the panel properties
        this.setLayout(new BorderLayout());
        this.setRoundTopLeft(10);
        this.setRoundTopRight(10);
        this.setPreferredSize(new Dimension(500, 45));
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 8));
        this.setBackground(Color.WHITE);

        // Create the panel for the search icon
        RoundedPanel searchIconPanel = new RoundedPanel(10);
        searchIconPanel.setLayout(new GridBagLayout());
        searchIconPanel.setBackground(Color.WHITE);
        searchIconPanel.setPreferredSize(new Dimension(45, 45));

        ImageIcon searchIcon = new SvgIcon(iconPath, 22).getImageIcon();
        JLabel searchIconLabel = new JLabel(searchIcon);
        searchIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        searchIconLabel.setVerticalAlignment(SwingConstants.CENTER);
        searchIconLabel.setPreferredSize(new Dimension(45, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        searchIconPanel.add(searchIconLabel, gbc);

        // Create the panel for the search text field
        RoundedPanel searchCenterPanel = new RoundedPanel();
        searchCenterPanel.setLayout(new BoxLayout(searchCenterPanel, BoxLayout.Y_AXIS));

        // Create the search text field with placeholder
        searchTextField = new JTextField(20) {
            private final String placeholder = placeholderText;
            private boolean isPlaceholder = true;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isPlaceholder && getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = getInsets().left + 5;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, x, y);
                    g2.dispose();
                }
            }

            @Override
            protected void processFocusEvent(FocusEvent e) {
                super.processFocusEvent(e);
                if (e.getID() == FocusEvent.FOCUS_GAINED) {
                    if (getText().isEmpty()) {
                        isPlaceholder = false;
                    }
                } else if (e.getID() == FocusEvent.FOCUS_LOST) {
                    if (getText().isEmpty()) {
                        isPlaceholder = true;
                    }
                }
                repaint();
            }
        };

        searchTextField.setHorizontalAlignment(SwingConstants.LEFT);
        searchTextField.setBorder(BorderFactory.createEmptyBorder());

        searchCenterPanel.add(searchTextField);

        // Add the icon panel and the text field panel to the main panel
        this.add(searchIconPanel, BorderLayout.WEST);
        this.add(searchCenterPanel, BorderLayout.CENTER);
    }

    // Method to get the text from the search text field
    public String getTextFieldText() {
        return searchTextField.getText();
    }

    // Method to get the search text field component directly
    public JTextField getSearchTextField() {
        return searchTextField;
    }
}
