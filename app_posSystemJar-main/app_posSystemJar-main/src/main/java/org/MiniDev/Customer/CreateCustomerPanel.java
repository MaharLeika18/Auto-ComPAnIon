package org.MiniDev.Customer;

import DBConnection.DBConnection;
import SqlLoadersAndUpdater.CustomerLoader;
import UI.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Home.CreateHomePanel;
import org.MiniDev.Home.MiniDevPOS;
import org.MiniDev.OOP.CustomerManager;
import org.MiniDev.OOP.Customers;
import org.MiniDev.Order.CreateOrderPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import static UI.IconCreator.createResizedIcon;
import static UI.IconTextButton.createIconOrangeButton;
import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;
import static org.MiniDev.Cashier.CreateTodaySalePanel.createHeaderLabel;

public class CreateCustomerPanel {

    protected static JPanel customerLeftMainPanel;
    protected static JPanel customerRightMainPanel;
    protected static JLabel lastClickedCustomerNameLabel = new JLabel();
    protected static JLabel lastClickedCustomerIDLabel = new JLabel();
    protected static JLabel lastClickedCustomerEmailLabel = new JLabel();
    protected static JLabel lastClickedCustomerPhoneNumberLabel = new JLabel();
    protected static JLabel editScreenCIDLabel;
    protected static ImageAvatar profileAvatar;
    protected static ImageAvatar imageAvatar;

    public static Customers lastClickedCustomer = null;

    private static final String EDIT_CARD = "Edit";
    private static final String ADD_CARD = "Add";
    private static final String MAIN_CARD = "Main";

    private static JPanel cardPanel;
    private static CardLayout cardLayout;
    protected static PanelRound customerListHoldingPanel;

    protected static String[] newCustomerLabels = {"Name", "Phone Number", "Email", "Address", "City", "Notes"};
    protected static Map<String, RoundedTextField> newCustomerTextFieldMap = new HashMap<>();
    protected static ImageAvatar addNewAvatar;

    protected static ImageAvatar profileToEdit;
    protected static String[] showLabelNames = {"Name", "Phone Number", "Email", "Address", "City", "Notes"};
    protected static Map<String, RoundedTextField> textFieldMap = new HashMap<>();

    protected static JPanel customerMainPanel;

    public static JPanel createCustomersPanel() {
        customerMainPanel = new JPanel(new BorderLayout());
        cardLayout = new CardLayout(); // Initialize CardLayout
        cardPanel = new JPanel(cardLayout); // Panel that holds the cards

        // Add the sections to the cardPanel
        cardPanel.add(createAbstractPanel(), MAIN_CARD);
        cardPanel.add(createEditCustomerInfoPanel(lastClickedCustomer), EDIT_CARD);
        cardPanel.add(createNewAddCustomersSectionPanel(), ADD_CARD);

        // Create a panel to hold the cardPanel and side buttons
        JPanel leftPanel = createCustomerLeftPanel();
        customerMainPanel.add(leftPanel, BorderLayout.WEST);
        customerMainPanel.add(cardPanel, BorderLayout.CENTER);

        return customerMainPanel;
    }

    private static JPanel createCustomerLeftPanel() {
        customerLeftMainPanel = new JPanel(new BorderLayout());
        customerLeftMainPanel.setPreferredSize(new Dimension(550, 0));
        customerLeftMainPanel.setBorder(BorderFactory.createEmptyBorder(0, GAP, 10, 10));
        customerLeftMainPanel.setOpaque(true);

        customerLeftMainPanel.add(createHeaderCustomerPanel(), BorderLayout.NORTH);
        customerLeftMainPanel.add(createCustomerMainPane(), BorderLayout.CENTER);

        return customerLeftMainPanel;
    }

    protected static JPanel createHeaderCustomerPanel() {
        JPanel topInnerPanel = new JPanel(new BorderLayout());
        topInnerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        topInnerPanel.setPreferredSize(new Dimension(0, 80));

        JLabel headerLabel = createHeaderLabel("Customers");
        JButton addNewCustomerButton = createIconOrangeButton("/PlusWhiteIcon.svg", 19, null, "Add New Customer", 14);
        addNewCustomerButton.setPreferredSize(new Dimension(150, 38));

        addNewCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, ADD_CARD); // Switch to add new customer section
                resetNewCustomerTextFields();
            }
        });

        topInnerPanel.add(headerLabel, BorderLayout.WEST);
        topInnerPanel.add(addNewCustomerButton, BorderLayout.EAST);

        return topInnerPanel;
    }

    protected static void filterCustomers(String query) {
        // Clear the current customer list display
        JPanel customerListsPanel = (JPanel) customerScrollPane.getViewport().getView();
        customerListsPanel.removeAll();

        // Filter the customer list based on the query
        List<Customers> filteredCustomers = customers.stream()
                .filter(customer -> customer.getCustomerName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());


        // Rebuild the customer list panel with the filtered results
        for (int i = 0; i < filteredCustomers.size(); i++) {
            Customers customer = filteredCustomers.get(i);
            // Pass the index to the method to handle alternating row colors
            PanelRound customerPanel = createCustomerPortfolioRowsPanel(customer, i);
            customerListsPanel.add(customerPanel);
        }

        // Refresh the customer list display
        customerListsPanel.revalidate();
        customerListsPanel.repaint();
    }


    protected static PanelRound createCustomerPageTopSearchPanel() {
        // Create an instance of SearchTopPaneCreator with a placeholder and icon
        SearchTopPaneCreator topSearchPanel = new SearchTopPaneCreator("Search Customer Name.", "/SearchIcon.svg");

        // Retrieve the search text field from the topSearchPanel
        JTextField searchField = topSearchPanel.getSearchTextField();

        // Add a DocumentListener to listen for changes in the search field
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterCustomers(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterCustomers(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterCustomers(searchField.getText());
            }
        });

        // Return the fully constructed top search panel
        return topSearchPanel;
    }


    protected static RoundedPanel createCustomerMainPane() {
        RoundedPanel customerMainPanel = new RoundedPanel(10);
        customerMainPanel.setLayout(new BorderLayout());
        customerMainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        customerMainPanel.add(createCustomerPageTopSearchPanel(), BorderLayout.NORTH);
        customerMainPanel.add(createCustomerListHoldingPane(), BorderLayout.CENTER);

        return customerMainPanel;
    }


    public static PanelRound createCustomerListHoldingPane() {
        // Create the RoundedPanel
        customerListHoldingPanel = new PanelRound();
        customerListHoldingPanel.setRoundBottomLeft(10);
        customerListHoldingPanel.setRoundBottomRight(10);
        customerListHoldingPanel.setLayout(new BorderLayout());


        customerListHoldingPanel.add(createLastClickedCustomerInfoPane(), BorderLayout.NORTH);
        customerListHoldingPanel.add(recentlyClickedCustomerInfoPane(), BorderLayout.CENTER);


        return customerListHoldingPanel;
    }

    protected static PanelRound recentlyClickedCustomerInfoPane() {
        PanelRound recentlyClickedCustomerInfoPane = new PanelRound();
        recentlyClickedCustomerInfoPane.setRoundBottomLeft(10);
        recentlyClickedCustomerInfoPane.setRoundBottomRight(10);
        recentlyClickedCustomerInfoPane.setLayout(new BorderLayout());

        JPanel headerRecentPanel = new JPanel(new BorderLayout());
        headerRecentPanel.setPreferredSize(new Dimension(0, 50));
        headerRecentPanel.setBorder(BorderFactory.createEmptyBorder(0, GAP, 5, 0));
        headerRecentPanel.setBackground(COLOR_WHITE);

        JLabel recentHeaderLabel = createHeaderLabel("Recent Customers");
        headerRecentPanel.add(recentHeaderLabel, BorderLayout.WEST);

        recentlyClickedCustomerInfoPane.add(headerRecentPanel, BorderLayout.NORTH);
        recentlyClickedCustomerInfoPane.add(createCustomerListsScrollPane(), BorderLayout.CENTER);

        return recentlyClickedCustomerInfoPane;
    }

    public static JScrollPane customerScrollPane;
    protected static CustomerManager customerManager;
    protected static List<Customers> customers;

    public static PanelRound createCustomerPortfolioRowsPanel(Customers customer, int rowIndex) {
        Color backgroundColor = (rowIndex % 2 == 0) ? COLOR_WHITE : COLOR_PANEL_GRAY;

        PanelRound customerInfoMainPanel = new PanelRound(backgroundColor);
        customerInfoMainPanel.setRoundBottomLeft(10);
        customerInfoMainPanel.setRoundBottomRight(10);
        customerInfoMainPanel.setRoundTopLeft(10);
        customerInfoMainPanel.setRoundTopRight(10);
        customerInfoMainPanel.setLayout(new BorderLayout());
        customerInfoMainPanel.setPreferredSize(new Dimension(490, 60));
        customerInfoMainPanel.setMaximumSize(new Dimension(490, 60));
        customerInfoMainPanel.setMinimumSize(new Dimension(490, 60));

        final Color originalBackgroundColor = backgroundColor;

        String dateText = (customer.getRegisteredDate() != null) ? customer.getRegisteredDate().toString() : "16/02/2024";

        customerInfoMainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Play the sound directly on the event dispatch thread
                SwingUtilities.invokeLater(() -> SoundEffect.playClickSound());

                // Use a SwingWorker for background tasks
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        lastClickedCustomer = customer; // Assume customer is defined elsewhere
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            SwingUtilities.invokeLater(() -> {
                                updateAllLabelsInfoForLastClick(lastClickedCustomer);
                                customerInfoMainPanel.revalidate();
                                customerInfoMainPanel.repaint();
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace(); // Handle any exceptions that occurred in doInBackground
                        }
                    }
                };
                worker.execute();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                customerInfoMainPanel.setBackground(COLOR_PANEL_HOVER); // Highlight on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                customerInfoMainPanel.setBackground(originalBackgroundColor); // Revert highlight on exit
            }
        });

        PanelRound portfolioLeft = new PanelRound(backgroundColor);
        PanelRound portfolioRight = new PanelRound(customerInfoMainPanel.getBackgroundColor());

        portfolioLeft.setRoundBottomLeft(10);
        portfolioLeft.setRoundTopLeft(10);
        portfolioLeft.setPreferredSize(new Dimension(80, 0));
        portfolioLeft.setLayout(new BorderLayout());
        portfolioLeft.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        profileAvatar = new ImageAvatar();
        profileAvatar.setFillRect(false);
        profileAvatar.repaint();

        if (customer.getProfilePicture() == null) {
            profileAvatar.setImage(getProfilePictureDefault());
        } else {
            profileAvatar.setImage(customer.getProfilePicture());
        }
        portfolioLeft.add(profileAvatar, BorderLayout.CENTER);

        portfolioRight.setRoundBottomRight(10);
        portfolioRight.setRoundTopRight(10);
        portfolioRight.setLayout(new GridLayout(2, 1));
        portfolioRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        PanelRound firstLayerInfo = new PanelRound(customerInfoMainPanel.getBackgroundColor());
        PanelRound secondLayerInfo = new PanelRound(customerInfoMainPanel.getBackgroundColor());

        firstLayerInfo.setLayout(new BorderLayout());
        secondLayerInfo.setLayout(new BorderLayout());

        firstLayerInfo.setRoundTopRight(10);
        secondLayerInfo.setRoundBottomRight(10);

        JLabel customerNameLabel = new JLabel(customer.getCustomerName());
        JLabel emailLabel = new JLabel(customer.getEmailAddress());

        // Divide by 1000 and format to "#,###.0 K"
        DecimalFormat df = new DecimalFormat("#,###");
        String formattedTotal = df.format(customer.getPurchasedGrandTotal() / 1000) + " K";

        JLabel purchasedGrandTotalK = new JLabel(formattedTotal);

        customerNameLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 14));
        emailLabel.setForeground(COLOR_FONT_GRAY);
        purchasedGrandTotalK.setForeground(COLOR_FONT_GRAY);

        JLabel registeredDateLabel = new JLabel(dateText);

        firstLayerInfo.add(customerNameLabel, BorderLayout.WEST);
        firstLayerInfo.add(registeredDateLabel, BorderLayout.EAST);

        secondLayerInfo.add(emailLabel, BorderLayout.WEST);
        secondLayerInfo.add(purchasedGrandTotalK, BorderLayout.EAST);

        portfolioRight.add(firstLayerInfo);
        portfolioRight.add(secondLayerInfo);

        customerInfoMainPanel.add(portfolioLeft, BorderLayout.WEST);
        customerInfoMainPanel.add(portfolioRight, BorderLayout.CENTER);

        return customerInfoMainPanel;
    }

    protected static JScrollPane createCustomerListsScrollPane() {
        // Create the panel to hold the customer list
        PanelRound customerListsPanel = new PanelRound();
        customerListsPanel.setLayout(new BoxLayout(customerListsPanel, BoxLayout.Y_AXIS));
        customerListsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        customerListsPanel.setRoundBottomRight(10);
        customerListsPanel.setRoundBottomLeft(10);

        // Load customer data
        CustomerLoader.loadCustomerData();
        // Get the singleton instance of CustomerManager
        customerManager = CustomerManager.getInstance();
        customers = customerManager.getAllCustomers();

        // Add a panel for each customer
        if (customers != null) {
            for (int i = 0; i < customers.size(); i++) {
                Customers customer = customers.get(i);
                PanelRound customerPanel = createCustomerPortfolioRowsPanel(customer, i);
                customerListsPanel.add(customerPanel);
            }
        } else {
            System.out.println("No customers available.");
        }

        // Create the JScrollPane
        customerScrollPane = new JScrollPane(customerListsPanel);
        customerScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        customerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        customerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        customerScrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(COLOR_GRAY));

        return customerScrollPane;
    }

    public static void refreshCustomerList(JScrollPane scrollPane) {
        // Clear existing customer data in CustomerManager
        customerManager.clearCustomers();

        // Reload the customer data from the database
        CustomerLoader.loadCustomerData();

        // Get updated customer list
        customers = customerManager.getAllCustomers();

        // Get the viewport view of the JScrollPane
        Component viewportView = scrollPane.getViewport().getView();

        // Check if the view is an instance of PanelRound
        if (viewportView instanceof PanelRound customerListsPanel) {

            // Clear existing panels
            customerListsPanel.removeAll();

            // Add updated customer panels
            if (customers != null) {
                for (int i = 0; i < customers.size(); i++) {
                    Customers customer = customers.get(i);
                    PanelRound customerPanel = createCustomerPortfolioRowsPanel(customer, i);
                    customerListsPanel.add(customerPanel);
                }
            } else {
                System.out.println("No customers available.");
            }

            // Refresh the panel and scroll pane
            customerListsPanel.revalidate();
            customerListsPanel.repaint();
        } else {
            System.out.println("Viewport view is not an instance of PanelRound.");
        }
    }


    public static byte[] getProfilePictureDefault() {
        try {
            // Load the image as a BufferedImage
            InputStream imageStream = ImageAvatar.class.getResourceAsStream("/ProfileIcon.png");
            BufferedImage bufferedImage = ImageIO.read(imageStream);

            // Convert the BufferedImage to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected static void resetNewCustomerTextFields() {
        newCustomerTextFieldMap.get("Name").setText("");
        newCustomerTextFieldMap.get("Phone Number").setText("");
        newCustomerTextFieldMap.get("Email").setText("");
        newCustomerTextFieldMap.get("Address").setText("");
        newCustomerTextFieldMap.get("City").setText("");
        newCustomerTextFieldMap.get("Notes").setText("");
        addNewAvatar.removeImage();

    }


    protected static void resetLastClickedCustomerInfo(boolean isNewCustomer) {
        if (isNewCustomer) {
            newCustomerTextFieldMap.get("Name").removeAll();
            newCustomerTextFieldMap.get("Phone Number").removeAll();
            newCustomerTextFieldMap.get("Email").removeAll();
            newCustomerTextFieldMap.get("Address").removeAll();
            newCustomerTextFieldMap.get("City").removeAll();
            newCustomerTextFieldMap.get("Notes").removeAll();
            addNewAvatar.removeImage();
        } else {
            editScreenCIDLabel.setText("");
            lastClickedCustomerNameLabel.setText("");
            lastClickedCustomerEmailLabel.setText("");
            lastClickedCustomerPhoneNumberLabel.setText("");
            lastClickedCustomerIDLabel.setText("");
            imageAvatar.removeImage();

            newCustomerTextFieldMap.get("Name").setText("");
            newCustomerTextFieldMap.get("Phone Number").setText("");
            newCustomerTextFieldMap.get("Email").setText("");
            newCustomerTextFieldMap.get("Address").setText("");
            newCustomerTextFieldMap.get("City").setText("");
            newCustomerTextFieldMap.get("Notes").setText("");
        }
    }


    protected static void updateAllLabelsInfoForLastClick(Customers lastClickedCustomer) {
        // Update labels with customer information
        editScreenCIDLabel.setText(lastClickedCustomer.getcID());
        lastClickedCustomerNameLabel.setText(lastClickedCustomer.getCustomerName() != null ? lastClickedCustomer.getCustomerName() : "N/A");
        lastClickedCustomerEmailLabel.setText(lastClickedCustomer.getEmailAddress() != null ? lastClickedCustomer.getEmailAddress() : "N/A");
        lastClickedCustomerPhoneNumberLabel.setText(lastClickedCustomer.getPhoneNumber() != null ? lastClickedCustomer.getPhoneNumber() : "N/A");
        lastClickedCustomerIDLabel.setText(lastClickedCustomer.getcID() != null ? lastClickedCustomer.getcID() : "N/A");
        if (lastClickedCustomer.getProfilePicture() == null) {
            imageAvatar.setImage(getProfilePictureDefault());
        } else {
            imageAvatar.setImage(lastClickedCustomer.getProfilePicture());
        }
        makeEditScreenPreparation(lastClickedCustomer);
    }

    protected static void resetForLastClickedInfoBox() {
        imageAvatar.removeImage();
        lastClickedCustomerNameLabel.setText("");
        lastClickedCustomerIDLabel.setText("");
        lastClickedCustomerEmailLabel.setText("");
        lastClickedCustomerPhoneNumberLabel.setText("");
    }

    protected static void resetEditTextFields() {
        profileToEdit.removeImage();
        editScreenCIDLabel.setText("");
        textFieldMap.get("Name").setText("");
        textFieldMap.get("Phone Number").setText("");
        textFieldMap.get("Email").setText("");
        textFieldMap.get("Address").setText("");
        textFieldMap.get("City").setText("");
        textFieldMap.get("Notes").setText("");
    }

    protected static void updateForLastClickedInfoBox(boolean isNewCustomer) {
        // Determine the appropriate text field map and avatar
        Map<String, RoundedTextField> textFieldMapToUse = isNewCustomer ? newCustomerTextFieldMap : textFieldMap;
        ImageAvatar avatarToUse = isNewCustomer ? addNewAvatar : profileToEdit;

        // Update customer name label
        lastClickedCustomerNameLabel.setText(getTextOrDefault(textFieldMapToUse.get("Name").getText()));

        // Update customer email label
        lastClickedCustomerEmailLabel.setText(getTextOrDefault(textFieldMapToUse.get("Email").getText()));

        // Update customer phone number label
        lastClickedCustomerPhoneNumberLabel.setText(getTextOrDefault(textFieldMapToUse.get("Phone Number").getText()));

        // Update customer ID label if not a new customer
        if (!isNewCustomer) {
            lastClickedCustomerIDLabel.setText(getTextOrDefault(lastClickedCustomer.getcID()));
        } else {
            lastClickedCustomerIDLabel.setText("");
        }

        // Update avatar image
        if (avatarToUse.getImage() == null) {
            imageAvatar.setImage(getProfilePictureDefault());
        } else {
            imageAvatar.setImage(avatarToUse.getImage());
        }
    }

    private static String getTextOrDefault(String text) {
        return (text != null && !text.trim().isEmpty()) ? text : "";
    }


    protected static JPanel createLastClickedCustomerInfoPane() {
        JPanel lastClickedCustomerInfoPane = new JPanel(new BorderLayout());
        lastClickedCustomerInfoPane.setPreferredSize(new Dimension(150, 150));

        lastClickedCustomerInfoPane.add(createCustomerAvatarPhotoPanel(), BorderLayout.WEST);
        lastClickedCustomerInfoPane.add(createInnerRightCustomerHoldingPane(), BorderLayout.CENTER);

        return lastClickedCustomerInfoPane;
    }


    public static JPanel createCustomerAvatarPhotoPanel() {
        JPanel customerAvatarPhotoPanel = new JPanel(new BorderLayout());
        customerAvatarPhotoPanel.setPreferredSize(new Dimension(140, 0));
        customerAvatarPhotoPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        customerAvatarPhotoPanel.setBackground(Color.WHITE);

        imageAvatar = new ImageAvatar();
        imageAvatar.setFillRect(false);
        imageAvatar.repaint();

        customerAvatarPhotoPanel.add(imageAvatar, BorderLayout.CENTER);

        return customerAvatarPhotoPanel;
    }


    protected static JPanel createInnerRightCustomerHoldingPane() {
        JPanel innerRightCustomerHoldingPane = new JPanel(new GridLayout(3, 1, 0, 0));

        JPanel firstLayerInfoPanel = new JPanel();
        JPanel secondLayerInfoPanel = new JPanel();
        JPanel thirdLayerInfoPanel = new JPanel();

        firstLayerInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        firstLayerInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 0));
        firstLayerInfoPanel.setBackground(Color.WHITE);

        secondLayerInfoPanel.setLayout(new GridLayout(2, 1, 0, 0));
        secondLayerInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        secondLayerInfoPanel.setBackground(Color.WHITE);

        thirdLayerInfoPanel.setLayout(new BorderLayout());
        thirdLayerInfoPanel.setBorder(BorderFactory.createEmptyBorder(7, 0, 10, 7));
        thirdLayerInfoPanel.setBackground(Color.WHITE);

        JPanel leftInner = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightInner = new JPanel(new BorderLayout());

        leftInner.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        leftInner.setBackground(Color.WHITE);
        leftInner.add(getEditButton());
        leftInner.add(getDeleteButton());

        rightInner.setPreferredSize(new Dimension(100, 100));
        rightInner.setBackground(COLOR_WHITE);
        rightInner.add(getAddToOrderButton());

        thirdLayerInfoPanel.add(leftInner, BorderLayout.CENTER);
        thirdLayerInfoPanel.add(rightInner, BorderLayout.EAST);

        // Align labels within the panel
        lastClickedCustomerNameLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 24));
        lastClickedCustomerNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lastClickedCustomerIDLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add labels to the panel
        firstLayerInfoPanel.add(lastClickedCustomerNameLabel);
        firstLayerInfoPanel.add(lastClickedCustomerIDLabel);

        lastClickedCustomerEmailLabel.setForeground(COLOR_FONT_GRAY);
        lastClickedCustomerPhoneNumberLabel.setForeground(COLOR_FONT_GRAY);
        lastClickedCustomerPhoneNumberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lastClickedCustomerEmailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        secondLayerInfoPanel.add(lastClickedCustomerEmailLabel);
        secondLayerInfoPanel.add(lastClickedCustomerPhoneNumberLabel);


        innerRightCustomerHoldingPane.add(firstLayerInfoPanel);
        innerRightCustomerHoldingPane.add(secondLayerInfoPanel);
        innerRightCustomerHoldingPane.add(thirdLayerInfoPanel);

        return innerRightCustomerHoldingPane;
    }

    private static LinkButton getEditButton() {
        LinkButton editButton = getEditLinkButton();

        // Set up button listeners
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lastClickedCustomer == null || editScreenCIDLabel.getText().isBlank()) {
                    showWarningClickFirst();
                } else {
                    makeEditScreenPreparation(lastClickedCustomer);
                    cardLayout.show(cardPanel, EDIT_CARD); // Switch to edit section
                }
            }
        });

        // Add action listener for button click

        return editButton;
    }

    private static LinkButton getEditLinkButton() {
        ImageIcon editIcon = new SvgIcon("/EditIcon.svg", 14).getImageIcon();
        LinkButton editButton = new LinkButton("Edit", editIcon);
        // Set the icon separately
        editButton.setIcon(editIcon);
        editButton.setForeground(COLOR_BLACK);

        // Configure text and icon alignment
        editButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Text to the right of the icon
        editButton.setVerticalTextPosition(SwingConstants.CENTER);
        editButton.setHorizontalAlignment(SwingConstants.CENTER);
        editButton.setVerticalAlignment(SwingConstants.CENTER);
        return editButton;
    }

    private static LinkButton getDeleteButton() {
        LinkButton deleteBinButton = getDeleteLinkButton();

        deleteBinButton.addActionListener(e -> {
            if (lastClickedCustomer == null || editScreenCIDLabel.getText().isBlank()) {
                DialogCollection.showCustomDialog(new RoundedPanel(10), "Please select the customer.", "Select the customer first.");
            } else {
                deleteCustomer(lastClickedCustomer);
                resetForLastClickedInfoBox();
                resetEditTextFields();
                refreshCustomerInfoLists();
                DialogCollection.showCustomDialog(new RoundedPanel(10), "Successfully deleted", "Successfully deleted");
            }
        });

        // Add action listener for button click
        return deleteBinButton;
    }

    private static LinkButton getDeleteLinkButton() {
        ImageIcon binIcon = new SvgIcon("/BinIcon.svg", 14).getImageIcon();
        LinkButton deleteBinButton = new LinkButton("Delete", binIcon);
        // Set the icon separately
        deleteBinButton.setIcon(binIcon);
        deleteBinButton.setForeground(COLOR_BLACK);

        // Configure text and icon alignment
        deleteBinButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Text to the right of the icon
        deleteBinButton.setVerticalTextPosition(SwingConstants.CENTER);
        deleteBinButton.setHorizontalAlignment(SwingConstants.CENTER);
        deleteBinButton.setVerticalAlignment(SwingConstants.CENTER);
        return deleteBinButton;
    }

    public static void refreshCustomerInfoLists() {
        refreshCustomerList(customerScrollPane);
    }


    private static RoundedBorderButton getAddToOrderButton() {
        RoundedBorderButton addToOrderButton = new RoundedBorderButton("Add To Order", 80, 50, COLOR_ORANGE);
        addToOrderButton.setPreferredSize(new Dimension(80, 50));
        addToOrderButton.setMinimumSize(new Dimension(80, 50));
        addToOrderButton.setMaximumSize(new Dimension(80, 50));
        addToOrderButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        addToOrderButton.setForeground(COLOR_ORANGE);
        addToOrderButton.setBackground(COLOR_BUTTON_ORANGE);

        addToOrderButton.addActionListener(e -> {
            addCustomerToOrder(MiniDevPOS.getCurrentParentPanelName());
        });

        return addToOrderButton;
    }

    private static void addCustomerToOrder(String previousParentText) {
        if (previousParentText.equals("Home") || previousParentText.isEmpty()) {
            MiniDevPOS.showPanel("Home");
            if (lastClickedCustomer.getCustomerName().isEmpty()) {
                JOptionPane.showMessageDialog(customerMainPanel, "You have selected no customer.", "Selected no customer", JOptionPane.ERROR_MESSAGE);
            }
            CreateHomePanel.showCustomerInfo(lastClickedCustomer.getCustomerName(), lastClickedCustomer.getcID());
        } else if (previousParentText.equals("Orders")) {
            MiniDevPOS.showPanel("Orders");
            if (lastClickedCustomer.getCustomerName().isEmpty()) {
                JOptionPane.showMessageDialog(customerMainPanel, "You have selected no customer.", "Selected no customer", JOptionPane.ERROR_MESSAGE);
            }
            CreateOrderPanel.setCustomerPanelVisibilityOnOrderPage(true, lastClickedCustomer.getCustomerName(), lastClickedCustomer.getcID());
        }
    }


    private static JPanel createAbstractPanel() {
        customerRightMainPanel = new JPanel();
        return customerRightMainPanel;
    }

    private static void makeEditScreenPreparation(Customers lastClickedCustomer) {
        profileToEdit.setImage(lastClickedCustomer.getProfilePicture());
        // Set text fields
        textFieldMap.get("Name").setText(lastClickedCustomer.getCustomerName());
        textFieldMap.get("Phone Number").setText(lastClickedCustomer.getPhoneNumber());
        textFieldMap.get("Email").setText(lastClickedCustomer.getEmailAddress());
        textFieldMap.get("Address").setText(lastClickedCustomer.getCustomerAddress());
        textFieldMap.get("City").setText(lastClickedCustomer.getCustomerCity());
        textFieldMap.get("Notes").setText(lastClickedCustomer.getCustomerNotes());
    }


    private static void showWarningClickFirst() {
        DialogCollection.showCustomDialog(new JFrame(), "Click customer to edit.", "Click customer to edit first.");
    }


    private static IconTextButton getUpdateCustomerButton(boolean isNewCustomer) {
        ImageIcon updateIcon = createResizedIcon("/UpdateIcon.svg", 20, null);
        IconTextButton updateButton = getUpdateButton(updateIcon);

        updateButton.addActionListener(e -> {
            if (isNewCustomer) {
                if (newCustomerTextFieldMap.get("Name").getText().isBlank() || newCustomerTextFieldMap.get("Phone Number").getText().isBlank()) {
                    DialogCollection.showCustomDialog(new RoundedPanel(10), "Enter customer name and phone number.", "Enter customer info.");
                } else {
                    createNewCustomer();
                    updateForLastClickedInfoBox(true);
                    resetNewCustomerTextFields();
                    resetEditTextFields();
                    refreshCustomerInfoLists();
                    DialogCollection.showCustomDialog(new RoundedPanel(10), "New Customer Added.", "New Customer Added.");
                    cardLayout.show(cardPanel, MAIN_CARD);
                }
            } else {
                createUpdateCustomerInfoIntoDatabase(lastClickedCustomer);
                updateForLastClickedInfoBox(false);
                refreshCustomerInfoLists();
                DialogCollection.showCustomDialog(new RoundedPanel(10), "Successfully updated.", "Successfully updated");
            }

        });

        return updateButton;
    }

    private static IconTextButton getUpdateButton(ImageIcon updateIcon) {
        IconTextButton updateButton = new IconTextButton("Update", updateIcon, 14, (COLOR_GREEN), 0);
        updateButton.setBackground(COLOR_GREEN);
        updateButton.setForeground(COLOR_WHITE);
        updateButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        updateButton.setPreferredSize(new Dimension(160, 40));

        updateButton.setIcon(updateIcon);

        updateButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        updateButton.setVerticalTextPosition(SwingConstants.CENTER);
        updateButton.setHorizontalAlignment(SwingConstants.CENTER);
        updateButton.setVerticalAlignment(SwingConstants.CENTER);
        return updateButton;
    }


    private static RoundedBorderButton getPanelCloseButton() {
        ImageIcon panelCloseIcon = createResizedIcon("/CloseIcon.svg", 20, null);
        RoundedBorderButton panelCloseButton = new RoundedBorderButton("Close", 160, 40, COLOR_ORANGE);
        panelCloseButton.setIcon(panelCloseIcon);

        panelCloseButton.setBackground(Color.WHITE);
        panelCloseButton.setForeground(COLOR_ORANGE);
        panelCloseButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        panelCloseButton.setPreferredSize(new Dimension(160, 40));

        // Align text and icon
        panelCloseButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        panelCloseButton.setVerticalTextPosition(SwingConstants.CENTER);
        panelCloseButton.setHorizontalAlignment(SwingConstants.CENTER);
        panelCloseButton.setVerticalAlignment(SwingConstants.CENTER);

        panelCloseButton.addActionListener(e -> cardLayout.show(cardPanel, MAIN_CARD));

        return panelCloseButton;
    }


    private static JPanel createUserInputRowsPanel(JLabel inputLabel, String textFieldName, boolean isNewCustomer) {
        JPanel mainSingleInputHoldingPanel = new JPanel();
        mainSingleInputHoldingPanel.setBackground(COLOR_WHITE);
        mainSingleInputHoldingPanel.setLayout(new BorderLayout());
        mainSingleInputHoldingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(COLOR_WHITE);
        labelPanel.setLayout(new BorderLayout());
        labelPanel.setPreferredSize(new Dimension(0, 30));

        JPanel textFieldPanel = new JPanel();
        textFieldPanel.setBackground(COLOR_WHITE);
        textFieldPanel.setLayout(new BorderLayout());

        RoundedTextField inputTextField = new RoundedTextField(10, 2, COLOR_LINE_COLOR);
        inputTextField.setName(textFieldName);
        inputTextField.setBackground(COLOR_WHITE);
        inputTextField.setEditable(true);
        inputTextField.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 14));
        inputTextField.setHorizontalAlignment(JTextField.LEFT); // Align the text to the left

        // Store the reference to the text field in the appropriate map based on the context
        if (isNewCustomer) {
            newCustomerTextFieldMap.put(textFieldName, inputTextField);
        } else {
            textFieldMap.put(textFieldName, inputTextField);
        }

        labelPanel.add(inputLabel, BorderLayout.WEST);
        textFieldPanel.add(inputTextField, BorderLayout.CENTER);

        mainSingleInputHoldingPanel.add(labelPanel, BorderLayout.NORTH);
        mainSingleInputHoldingPanel.add(textFieldPanel, BorderLayout.CENTER);

        return mainSingleInputHoldingPanel;
    }

    private static void createNewCustomer() {
        String cid = null;
        byte[] profilePicture = addNewAvatar.getImageBytes(); // Get the byte array of the image
        String customerName = newCustomerTextFieldMap.get("Name").getText();
        String teleNo = newCustomerTextFieldMap.get("Phone Number").getText();
        String emailAddress = newCustomerTextFieldMap.get("Email").getText();
        String customerAddress = newCustomerTextFieldMap.get("Address").getText();
        String customerCity = newCustomerTextFieldMap.get("City").getText();
        String notes = newCustomerTextFieldMap.get("Notes").getText();

        String sql = "{CALL sp_UpdateCustomer(?,?,?,?,?,?,?,?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            // Set the parameters
            stmt.setString(1, cid);
            stmt.setBytes(2, profilePicture); // Use the byte array here
            stmt.setString(3, customerName);
            stmt.setString(4, teleNo);
            stmt.setString(5, emailAddress);
            stmt.setString(6, customerAddress);
            stmt.setString(7, customerCity);
            stmt.setString(8, notes);

            // Execute the stored procedure
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void createUpdateCustomerInfoIntoDatabase(Customers lastClickedCustomer) {
        String cid = lastClickedCustomer.getcID();

        // Retrieve the most recent values from the text fields
        byte[] profilePicture = lastClickedCustomer.getProfilePicture(); // Assuming profile picture is not changed via a text field
        String customerName = textFieldMap.get("Name").getText();
        String teleNo = textFieldMap.get("Phone Number").getText();
        String emailAddress = textFieldMap.get("Email").getText();
        String customerAddress = textFieldMap.get("Address").getText();
        String customerCity = textFieldMap.get("City").getText();
        String notes = textFieldMap.get("Notes").getText();

        String sql = "{CALL sp_UpdateCustomer(?,?,?,?,?,?,?,?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            // Set the parameters
            stmt.setString(1, cid);
            stmt.setBytes(2, profilePicture);
            stmt.setString(3, customerName);
            stmt.setString(4, teleNo);
            stmt.setString(5, emailAddress != null ? emailAddress : "");
            stmt.setString(6, customerAddress != null ? customerAddress : "");
            stmt.setString(7, customerCity != null ? customerCity : "");
            stmt.setString(8, notes != null ? notes : "");

            // Execute the stored procedure
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void deleteCustomer(Customers lastClickedCustomer) {
        // Check if a customer is selected
        if (lastClickedCustomer.getcID() == null) {
            DialogCollection.showCustomDialog(new RoundedPanel(10), "Please select the customer.", "Select the customer first.");
            return; // Exit the method if no customer is selected
        }

        String cid = lastClickedCustomer.getcID();
        String sql = "{CALL sp_DeleteCustomer(?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            // Set the CID parameter
            stmt.setString(1, cid);

            // Execute the stored procedure
            stmt.execute();

            // Inform the user of successful deletion
            DialogCollection.showCustomDialog(new RoundedPanel(10), "Customer deleted successfully.", "Success");

        } catch (SQLException e) {
            // Handle specific error messages based on the SQLSTATE and error message
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Customer with the provided CID does not exist")) {
                DialogCollection.showCustomDialog(new RoundedPanel(10), "The customer does not exist.", "Deletion Failed");
            } else {
                // General error handling
                e.printStackTrace();
                DialogCollection.showCustomDialog(new RoundedPanel(10), "An error occurred while attempting to delete the customer.", "Error");
            }
        }
    }


    private static PanelRound createNewAddCustomersSectionPanel() {
        PanelRound addNewCustomerPanel = new PanelRound(COLOR_GRAY);
        addNewCustomerPanel.setLayout(new BorderLayout());
        addNewCustomerPanel.setBorder(BorderFactory.createEmptyBorder(GAP, 10, 10, GAP));

        RoundedPanel newInnerMainPanel = new RoundedPanel(10);
        newInnerMainPanel.setLayout(new BorderLayout());

        PanelRound userAvatarPanel = new PanelRound();
        PanelRound userInputPanel = new PanelRound();
        PanelRound buttonBottomPanel = new PanelRound();

        userInputPanel.setLayout(new GridLayout(6, 1, 1, 1));

        buttonBottomPanel.setRoundBottomRight(10);
        buttonBottomPanel.setRoundBottomLeft(10);
        buttonBottomPanel.setPreferredSize(new Dimension(0, 70));
        buttonBottomPanel.setLayout(new GridLayout(1, 2, GAP, GAP));
        buttonBottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, GAP, 10));

        userAvatarPanel.setLayout(new BorderLayout());
        userAvatarPanel.setRoundTopLeft(10);
        userAvatarPanel.setRoundTopRight(10);
        userAvatarPanel.setPreferredSize(new Dimension(500, 60));
        userAvatarPanel.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, 0));

        PanelRound innerRightAvatarPanel = new PanelRound();
        innerRightAvatarPanel.setRoundTopRight(10);
        innerRightAvatarPanel.setLayout(new BorderLayout());
        innerRightAvatarPanel.setBackground(COLOR_BLUE);
        innerRightAvatarPanel.setPreferredSize(new Dimension(60, 60));
        innerRightAvatarPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, GAP));

        addNewAvatar = new EditableAvatar();
        addNewAvatar.setImage(getProfilePictureDefault());

        // Add profileToEdit to userAvatarPanel, using BorderLayout.CENTER will center the component
        innerRightAvatarPanel.add(addNewAvatar, BorderLayout.CENTER);

        JLabel newRegistrationFormLabel = new JLabel("New Registration Customer");
        newRegistrationFormLabel.setForeground(COLOR_FONT_GRAY);
        newRegistrationFormLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 16));

        userAvatarPanel.add(newRegistrationFormLabel, BorderLayout.WEST);
        userAvatarPanel.add(innerRightAvatarPanel, BorderLayout.EAST);

        for (int i = 0; i < newCustomerLabels.length; i++) {
            JLabel label = new JLabel(newCustomerLabels[i]);
            label.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 14));
            label.setForeground(COLOR_BLACK);
            // Create the input row panel
            JPanel inputRowPanel = createUserInputRowsPanel(label, newCustomerLabels[i], true);
            userInputPanel.add(inputRowPanel);
        }

        buttonBottomPanel.add(getPanelCloseButton());
        buttonBottomPanel.add(getUpdateCustomerButton(true));


        newInnerMainPanel.add(userAvatarPanel, BorderLayout.NORTH);
        newInnerMainPanel.add(userInputPanel, BorderLayout.CENTER);
        newInnerMainPanel.add(buttonBottomPanel, BorderLayout.SOUTH);

        addNewCustomerPanel.add(newInnerMainPanel, BorderLayout.CENTER);

        return addNewCustomerPanel;
    }


    private static PanelRound createEditCustomerInfoPanel(Customers lastClickedCustomerToEdit) {
        PanelRound editPanel = new PanelRound(COLOR_GRAY);
        editPanel.setLayout(new BorderLayout());
        editPanel.setBorder(BorderFactory.createEmptyBorder(GAP, 10, 10, GAP));

        RoundedPanel editInnerMainPanel = new RoundedPanel(10);
        editInnerMainPanel.setLayout(new BorderLayout());

        PanelRound userAvatarPanel = new PanelRound();
        PanelRound userInputPanel = new PanelRound();
        PanelRound buttonBottomPanel = new PanelRound();

        userInputPanel.setLayout(new GridLayout(6, 1, 1, 1));

        buttonBottomPanel.setRoundBottomRight(10);
        buttonBottomPanel.setRoundBottomLeft(10);
        buttonBottomPanel.setPreferredSize(new Dimension(0, 70));
        buttonBottomPanel.setLayout(new GridLayout(1, 2, GAP, GAP));
        buttonBottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, GAP, 10));

        userAvatarPanel.setLayout(new BorderLayout());
        userAvatarPanel.setRoundTopLeft(10);
        userAvatarPanel.setRoundTopRight(10);
        userAvatarPanel.setPreferredSize(new Dimension(500, 60));
        userAvatarPanel.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, 0));

        PanelRound innerRightAvatarPanel = new PanelRound();
        innerRightAvatarPanel.setRoundTopRight(10);
        innerRightAvatarPanel.setLayout(new BorderLayout());
        innerRightAvatarPanel.setBackground(COLOR_BLUE);
        innerRightAvatarPanel.setPreferredSize(new Dimension(60, 60));
        innerRightAvatarPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, GAP));

        profileToEdit = new EditableAvatar();
        // Check if lastClickedCustomerToEdit is null
        if (lastClickedCustomerToEdit != null) {
            if (lastClickedCustomerToEdit.getProfilePicture() != null) {
                profileToEdit.setImage(lastClickedCustomerToEdit.getProfilePicture());
            } else {
                profileToEdit.setImage(getProfilePictureDefault()); // Set a default image if profile picture is null
            }
        } else {
            // Handle case where lastClickedCustomerToEdit is null
            profileToEdit.setImage(getProfilePictureDefault()); // Set a default image if lastClickedCustomerToEdit is null
        }
        // Add profileToEdit to userAvatarPanel, using BorderLayout.CENTER will center the component
        innerRightAvatarPanel.add(profileToEdit, BorderLayout.CENTER);

        editScreenCIDLabel = new JLabel();
        editScreenCIDLabel.setForeground(COLOR_FONT_GRAY);

        userAvatarPanel.add(editScreenCIDLabel, BorderLayout.WEST);
        userAvatarPanel.add(innerRightAvatarPanel, BorderLayout.EAST);


        for (int i = 0; i < showLabelNames.length; i++) {
            JLabel label = new JLabel(showLabelNames[i]);
            label.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 14));
            label.setForeground(COLOR_BLACK);

            // Create the input row panel
            JPanel inputRowPanel = createUserInputRowsPanel(label, showLabelNames[i], false);
            userInputPanel.add(inputRowPanel);
        }

        buttonBottomPanel.add(getPanelCloseButton());
        buttonBottomPanel.add(getUpdateCustomerButton(false));


        editInnerMainPanel.add(userAvatarPanel, BorderLayout.NORTH);
        editInnerMainPanel.add(userInputPanel, BorderLayout.CENTER);
        editInnerMainPanel.add(buttonBottomPanel, BorderLayout.SOUTH);

        editPanel.add(editInnerMainPanel, BorderLayout.CENTER);

        return editPanel;
    }


}
