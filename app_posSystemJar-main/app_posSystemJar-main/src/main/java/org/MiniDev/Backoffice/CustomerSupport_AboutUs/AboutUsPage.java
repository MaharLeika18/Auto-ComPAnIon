package org.MiniDev.Backoffice.CustomerSupport_AboutUs;

import UI.RoundedPanel;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.CreateSettingsPanel;
import org.apache.batik.swing.JSVGCanvas;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static UI.UserFinalSettingsVar.*;

public class AboutUsPage extends CreateSettingsPanel {

    protected JSVGCanvas svgCanvasAboutUs;

    public AboutUsPage() {

    }

    public JPanel aboutUsPage() {
        JPanel aboutUsPanel = new JPanel(new BorderLayout());
        aboutUsPanel.setOpaque(false);

        RoundedPanel mainHoldingPanel = new RoundedPanel(10);
        mainHoldingPanel.setOpaque(false);
        mainHoldingPanel.setBackground(COLOR_WHITE);
        mainHoldingPanel.setLayout(new BorderLayout());
        mainHoldingPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        mainHoldingPanel.add(getHeaderPanel(mainHoldingPanel), BorderLayout.NORTH);
        mainHoldingPanel.add(getAboutUsCanvas(), BorderLayout.CENTER);
        mainHoldingPanel.add(getAboutUsTextPanel(mainHoldingPanel), BorderLayout.WEST);

        aboutUsPanel.add(mainHoldingPanel, BorderLayout.CENTER);
        return aboutUsPanel;
    }

    private JPanel getHeaderPanel(RoundedPanel parentPanel) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBackground(COLOR_WHITE);
        headerPanel.setPreferredSize(new Dimension(parentPanel.getWidth(), 40));
        JLabel headerLabel = new JLabel("About Us");
        headerLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 24));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);  // Center text horizontally
        headerLabel.setVerticalAlignment(SwingConstants.CENTER);    // Center text vertically
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel getAboutUsCanvas() {
        JPanel canvasPanel = new JPanel(new BorderLayout());
        canvasPanel.setOpaque(false);
        canvasPanel.setBorder(BorderFactory.createEmptyBorder(140, 100, 140, 50));
        canvasPanel.setBackground(COLOR_WHITE);

        svgCanvasAboutUs = new JSVGCanvas();
        svgCanvasAboutUs.setOpaque(false);
        svgCanvasAboutUs.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        svgCanvasAboutUs.setPreferredSize(new Dimension(250, 250)); // Fixed size
        svgCanvasAboutUs.setMaximumSize(new Dimension(250, 250));
        svgCanvasAboutUs.setMinimumSize(new Dimension(250, 250));
        svgCanvasAboutUs.setEnableZoomInteractor(false); // Disable zoom
        svgCanvasAboutUs.setEnablePanInteractor(false); // Disable pan
        loadDefaultAboutUsImage();
        canvasPanel.add(svgCanvasAboutUs, BorderLayout.CENTER);
        return canvasPanel;
    }

    private void loadDefaultAboutUsImage() {
        try {
            URL defaultImageUrl = getClass().getResource("/AboutUsIllustration.svg");
            if (defaultImageUrl != null) {
                svgCanvasAboutUs.setURI(defaultImageUrl.toString());
                svgCanvasAboutUs.setPreferredSize(new Dimension(250, 250)); // Set preferred size
                svgCanvasAboutUs.revalidate(); // Revalidate after setting the URI
                svgCanvasAboutUs.repaint(); // Repaint to reflect changes
            } else {
                System.out.println("SVG file not found: /AboutUsIllustration.svg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel getAboutUsTextPanel(RoundedPanel parentPanel) {
        JPanel textHoldingPanel = new JPanel(new BorderLayout());
        textHoldingPanel.setOpaque(false);
        textHoldingPanel.setBackground(COLOR_WHITE);
        textHoldingPanel.setPreferredSize(new Dimension(550, parentPanel.getHeight()));
        textHoldingPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 15, 30));

        textHoldingPanel.add(getTextHeaderPanel(textHoldingPanel), BorderLayout.NORTH);
        textHoldingPanel.add(getTextBodyPanel(), BorderLayout.CENTER);

        return textHoldingPanel;
    }

    private JPanel getTextHeaderPanel(JPanel parentPanel) {
        JPanel textHeaderPanel = new JPanel(new BorderLayout());
        textHeaderPanel.setOpaque(false);  // Make panel transparent (if needed)
        textHeaderPanel.setPreferredSize(new Dimension(parentPanel.getWidth(), 65));  // Set preferred size
        String textHeaderString = "What Do You Know About Our Community And Our Story?";
        JTextArea textHeaderArea = getTextHeaderArea(textHeaderString);
        textHeaderPanel.add(textHeaderArea, BorderLayout.CENTER);
        return textHeaderPanel;
    }

    private JTextArea getTextHeaderArea(String textHeaderString) {
        JTextArea textHeaderArea = new JTextArea(textHeaderString);
        textHeaderArea.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 20));
        textHeaderArea.setWrapStyleWord(true);  // Wrap words at word boundaries
        textHeaderArea.setLineWrap(true);       // Enable line wrapping
        textHeaderArea.setOpaque(false);        // Make JTextArea background transparent
        textHeaderArea.setEditable(false);      // Make it non-editable (like a label)
        return textHeaderArea;
    }

    private JPanel getTextBodyPanel() {
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false); // Ensure the panel is opaque to see the background color
        bodyPanel.setBackground(COLOR_WHITE); // Set the background color (e.g., white)
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        // Create headers
        JLabel ourStoryHeader = bodyHeaderFactory("Our Story");
        JLabel philosophyHeader = bodyHeaderFactory("Philosophy");
        JLabel ourGoalHeader = bodyHeaderFactory("Our Goal");

        // Create body text with left alignment
        JTextArea ourStoryBodyText = bodyTextAreaFactory("In 2024, a group of passionate tech enthusiasts, entrepreneurs, " +
                "and innovators came together with a shared vision: to drive Myanmar's digital transformation " +
                "through collaboration, creativity, and technology. This vision led to the creation of " +
                "Burma MiniDev Community—a community-driven IT company dedicated to delivering innovative solutions, " +
                "fostering skill development, and shaping Myanmar’s tech future.");

        JTextArea philosophyBodyText = bodyTextAreaFactory("At MiniDev, we believe technology should simplify, not complicate, " +
                "business operations. Our Point of Sale (POS) software is built to be intuitive, efficient, and user-friendly, " +
                "enabling businesses of all sizes to thrive. We aim to provide flexible, reliable solutions that empower entrepreneurs " +
                "to streamline operations and enhance customer experiences with ease.");

        JTextArea ourGoalBodyText = bodyTextAreaFactory("Our goal is to equip Myanmar’s businesses with the tools to succeed in a digital world. " +
                "Through scalable and accessible solutions, like our POS software, we help businesses—from startups to established enterprises—improve " +
                "efficiency, make data-driven decisions, and grow.");
        JTextArea ourGoalBodyTextPartII = bodyTextAreaFactory("We are committed to building technology that drives progress and supports " +
                "Myanmar’s entrepreneurs on their journey to success.");


        // Add components to the body panel
        bodyPanel.add(ourStoryHeader);
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(ourStoryBodyText);
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(philosophyHeader);
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(philosophyBodyText);
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(ourGoalHeader);
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(ourGoalBodyText);
        bodyPanel.add(Box.createVerticalStrut(3));
        bodyPanel.add(ourGoalBodyTextPartII);


        return bodyPanel;
    }


    private JLabel bodyHeaderFactory(String headerText) {
        JLabel headerLabel = new JLabel(headerText);
        headerLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 16));
        headerLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        return headerLabel;
    }

    private JTextArea bodyTextAreaFactory(String bodyText) {
        JTextArea bodyTextArea = new JTextArea(bodyText);
        bodyTextArea.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14));
        bodyTextArea.setOpaque(false);        // Make JTextArea background transparent
        bodyTextArea.setEditable(false);      // Make it non-editable (like a label)
        bodyTextArea.setAlignmentX(0.0f);  // Set text alignment to the left for JTextArea
        bodyTextArea.setLineWrap(true);    // Enable line wrapping if text is too long for the width
        bodyTextArea.setWrapStyleWord(true); // Make sure text wraps at word boundaries
        bodyTextArea.setCaretPosition(0);  // Optional: Start caret position at the top
        bodyTextArea.setForeground(COLOR_FONT_GRAY);
        return bodyTextArea;
    }


}
