package com.oop.bd.praktikum;

import com.oop.bd.praktikum.controller.CategoryController;
import com.oop.bd.praktikum.controller.ProductController;
import com.oop.bd.praktikum.controller.WarehouseController;
import com.oop.bd.praktikum.dto.CategoryDTO;
import com.oop.bd.praktikum.dto.ProductDTO;
import com.oop.bd.praktikum.dto.WarehouseDTO;
import com.oop.bd.praktikum.entity.Category;
import com.oop.bd.praktikum.entity.Product;
import com.oop.bd.praktikum.entity.Warehouse;
import com.oop.bd.praktikum.service.CategoryService;
import com.oop.bd.praktikum.service.ProductService;
import com.oop.bd.praktikum.service.WarehouseService;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Component;

@Component
public class PraktikumApplication {

  // A custom editor to disable editing of the rows
  DefaultCellEditor editor = new DefaultCellEditor(new JTextField()) {
    @Override
    public boolean isCellEditable(EventObject e) {
      return false;
    }
  };

  // A custom DocumentFilter that allows only integers to be entered
  DocumentFilter integerFilter = new DocumentFilter() {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
        throws BadLocationException {
      if (string != null && string.matches("\\d*")) {
        super.insertString(fb, offset, string, attr);
      }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs)
        throws BadLocationException {
      if (string != null && string.matches("\\d*")) {
        super.replace(fb, offset, length, string, attrs);
      }
    }
  };

  private final CategoryController categoryController;
  private final WarehouseController warehouseController;
  private final ProductController productController;
  private final ProductService productService;
  private final CategoryService categoryService;
  private final WarehouseService warehouseService;
  private JComboBox<String> categoryComboBox;
  private JComboBox<String> warehouseComboBox;

  public PraktikumApplication(CategoryController categoryController,
      WarehouseController warehouseController,
      ProductController productController,
      ProductService productService,
      CategoryService categoryService,
      WarehouseService warehouseService) {
    this.categoryController = categoryController;
    this.warehouseController = warehouseController;
    this.productController = productController;
    this.productService = productService;
    this.categoryService = categoryService;
    this.warehouseService = warehouseService;
  }

  public void run() {
    SwingUtilities.invokeLater(() -> {
      JFrame mainFrame = new JFrame("Warehouse Management System");
      mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      mainFrame.setSize(1000, 550);
      mainFrame.setResizable(false);

      JTabbedPane tabbedPane = new JTabbedPane();

      // Add UI components for each entity
      tabbedPane.addTab("Categories", createCategoryPanel());
      JPanel productPanel = createProductPanel();
      tabbedPane.addTab("Products", productPanel);
      tabbedPane.addTab("Warehouses", createWarehousePanel());

      // Add ChangeListener to the JTabbedPane
      tabbedPane.addChangeListener(e -> {
        if (tabbedPane.getSelectedIndex() == 1) { // If the selected tab is Products
          fillCategoryComboBox(categoryComboBox); // Use the reference to the combo box
          fillWarehouseComboBox(warehouseComboBox);
        }
      });

      mainFrame.add(tabbedPane);
      mainFrame.setVisible(true);
    });
  }

  private JPanel createCategoryPanel() {
    JPanel categoryPanel = new JPanel(new BorderLayout());

    // Create and set up the JTable
    DefaultTableModel categoryTableModel = new DefaultTableModel(new Object[][]{},
        new String[]{"Name"});
    JTable categoryTable = new JTable(categoryTableModel);

    //Setting the editor in order to forbid double click editing of the rows
    categoryTable.setDefaultEditor(Object.class, editor);
    JScrollPane categoryTableScrollPane = new JScrollPane(categoryTable);
    categoryPanel.add(categoryTableScrollPane, BorderLayout.CENTER);

    // Create the input panel with JLabels and JTextFields
    JPanel inputPanel = new JPanel(new GridLayout(1, 2));
    inputPanel.add(new JLabel("Category Name:"));
    JTextField categoryNameField = new JTextField();
    inputPanel.add(categoryNameField);
    categoryPanel.add(inputPanel, BorderLayout.NORTH);

    // Create the buttons panel with JButtons for CRUD operations and search
    JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));
    JButton addButton = new JButton("Add");
    JButton editButton = new JButton("Edit");
    JButton deleteButton = new JButton("Delete");
    buttonsPanel.add(addButton);
    buttonsPanel.add(editButton);
    buttonsPanel.add(deleteButton);
    categoryPanel.add(buttonsPanel, BorderLayout.SOUTH);

    //Add button listener
    addButton.addActionListener(e -> {
      String categoryName = categoryNameField.getText();
      if (!categoryName.trim().isEmpty()) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(categoryName);
        categoryController.createCategory(categoryDTO);
        updateCategoryTable(categoryTableModel);
      }
    });

    //Edit button listener
    editButton.addActionListener(e -> {
      int selectedRow = categoryTable.getSelectedRow();
      if (selectedRow != -1) {
        String currentCategoryName = (String) categoryTableModel.getValueAt(selectedRow, 0);

        // Create a dialog to allow the user to edit the category name
        String newCategoryName = (String) JOptionPane.showInputDialog(
            categoryPanel,
            "Edit Category Name:",
            "Edit Category",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            currentCategoryName
        );

        // If the user has entered a new category name and it's different from the old one
        if (newCategoryName != null &&
            !newCategoryName.trim().isEmpty() &&
            !newCategoryName.equals(currentCategoryName)) {
          CategoryDTO categoryDTO = categoryController.getCategoryByName(currentCategoryName);
          categoryDTO.setName(newCategoryName);
          categoryController.updateCategory(currentCategoryName, categoryDTO);

          // Update the JTable with new data
          updateCategoryTable(categoryTableModel);
        }
      } else {
        JOptionPane.showMessageDialog(categoryPanel, "Please select a row to edit.",
            "No Row Selected", JOptionPane.WARNING_MESSAGE);
      }
    });

    //Delete button listener
    deleteButton.addActionListener(e -> {
      int selectedRow = categoryTable.getSelectedRow();

      if (selectedRow >= 0) {
        String currentCategoryName = (String) categoryTableModel.getValueAt(selectedRow, 0);

        try {
          categoryController.deleteCategory(currentCategoryName);
        } catch (NotFoundException ex) {
          throw new RuntimeException(ex.getMessage());
        }

        updateCategoryTable(categoryTableModel);
      }
    });
    return categoryPanel;
  }

  private JPanel createProductPanel() {
    JPanel productPanel = new JPanel(new BorderLayout());

    // Create and set up the JTable
    DefaultTableModel productTableModel = new DefaultTableModel(new Object[][]{},
        new String[]{"Name", "Quantity", "Category", "Warehouse"});
    JTable productTable = new JTable(productTableModel);

    //Setting the editor in order to forbid double click editing of the rows
    productTable.setDefaultEditor(Object.class, editor);
    JScrollPane productTableScrollPane = new JScrollPane(productTable);
    productPanel.add(productTableScrollPane, BorderLayout.CENTER);

    // Create the input panel with JLabels, JTextFields, and JComboBoxes
    JPanel inputPanel = new JPanel(new GridLayout(1, 8));

    inputPanel.add(new JLabel("Product Name:"));
    JTextField productNameField = new JTextField();
    inputPanel.add(productNameField);

    inputPanel.add(new JLabel("Quantity:"));
    JTextField productQuantityField = new JTextField();
    ((PlainDocument) productQuantityField.getDocument()).setDocumentFilter(integerFilter);
    inputPanel.add(productQuantityField);

    inputPanel.add(new JLabel("Category:"));
    this.categoryComboBox = new JComboBox<>(); // Save the reference to the combo box
    inputPanel.add(categoryComboBox);

    inputPanel.add(new JLabel("Warehouse:"));
    this.warehouseComboBox = new JComboBox<>();
    inputPanel.add(warehouseComboBox);
    productPanel.add(inputPanel, BorderLayout.NORTH);

    JButton refreshButton = new JButton("Reload table");
    refreshButton.addActionListener(e -> {
      updateProductTable(productTableModel);
    });
    inputPanel.add(refreshButton);

    // Create the buttons panel with JButtons for CRUD operations and search
    JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));
    JButton addButton = new JButton("Add");
    JButton editButton = new JButton("Edit");
    JButton searchButton = new JButton("Search");
    JButton deleteButton = new JButton("Delete");

    buttonsPanel.add(addButton);
    buttonsPanel.add(editButton);
    buttonsPanel.add(searchButton);
    buttonsPanel.add(deleteButton);
    productPanel.add(buttonsPanel, BorderLayout.SOUTH);

    // Add button listener and other listeners as needed
    addButton.addActionListener(e -> {
      String productName = productNameField.getText();
      String productQuantity = productQuantityField.getText();
      String categoryName = (String) categoryComboBox.getSelectedItem();
      String warehouseName = (String) warehouseComboBox.getSelectedItem();

      if (!productName.trim().isEmpty() && !productQuantity.trim().isEmpty() &&
          categoryName != null && warehouseName != null) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(productName);
        productDTO.setQuantity(Integer.parseInt(productQuantity));
        productDTO.setCategoryName(categoryName);
        productDTO.setWarehouseName(warehouseName);

        productController.createProduct(productDTO);
        updateProductTable(productTableModel);
      }
    });

    // Edit button listener
    editButton.addActionListener(e -> {

      String[] categories = categoryController.getAllCategories().stream()
          .map(CategoryDTO::getName)
          .toArray(String[]::new);

      String[] warehouses = warehouseController.getAllWarehouses().stream()
          .map(WarehouseDTO::getName)
          .toArray(String[]::new);

      int selectedRow = productTable.getSelectedRow();
      if (selectedRow != -1) {
        String currentProductName = (String) productTableModel.getValueAt(selectedRow, 0);
        ProductDTO productDTO = productController.getProductByName(currentProductName);

        // Create a custom JDialog with input components for each field
        JDialog editProductDialog = new JDialog((Frame) null, "Edit Product", true);
        editProductDialog.setSize(300, 200);
        editProductDialog.setLayout(new GridLayout(5, 2));

        // Create and add input components for each field
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(productDTO.getName());
        editProductDialog.add(nameLabel);
        editProductDialog.add(nameField);

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(Integer.toString(productDTO.getQuantity()));
        ((PlainDocument) quantityField.getDocument()).setDocumentFilter(integerFilter);
        editProductDialog.add(quantityLabel);
        editProductDialog.add(quantityField);

        JLabel categoryLabel = new JLabel("Category:");
        String[] dialogCategories = Arrays.copyOf(categories, categories.length);
        JComboBox<String> categoryEditBox = new JComboBox<>(dialogCategories);
        categoryEditBox.setSelectedItem(productDTO.getCategoryName());
        editProductDialog.add(categoryLabel);
        editProductDialog.add(categoryEditBox);

        JLabel warehouseLabel = new JLabel("Warehouse:");
        String[] dialogWarehouses = Arrays.copyOf(warehouses, warehouses.length);
        JComboBox<String> warehouseEditBox = new JComboBox<>(dialogWarehouses);
        warehouseEditBox.setSelectedItem(productDTO.getWarehouseName());
        editProductDialog.add(warehouseLabel);
        editProductDialog.add(warehouseEditBox);

        // Add OK and Cancel buttons
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        editProductDialog.add(okButton);
        editProductDialog.add(cancelButton);

        // OK button listener
        okButton.addActionListener(event -> {
          String newName = nameField.getText().trim();
          int newQuantity = Integer.parseInt(quantityField.getText().trim());
          String newCategoryName = (String) categoryEditBox.getSelectedItem();
          String newWarehouseName = (String) warehouseEditBox.getSelectedItem();

          if (!newName.isEmpty() && newQuantity >= 0 && !newCategoryName.isEmpty()
              && !newWarehouseName.isEmpty()) {
            productDTO.setName(newName);
            productDTO.setQuantity(newQuantity);
            productDTO.setCategoryName(newCategoryName);
            productDTO.setWarehouseName(newWarehouseName);

            productController.updateProductByName(currentProductName, productDTO);

            // Update the JTable with new data
            updateProductTable(productTableModel);

            // Close the edit dialog
            editProductDialog.dispose();
          } else {
            JOptionPane.showMessageDialog(editProductDialog,
                "All fields must be filled out correctly.",
                "Invalid Input", JOptionPane.WARNING_MESSAGE);
          }
        });

        // Cancel button listener
        cancelButton.addActionListener(event -> editProductDialog.dispose());

        editProductDialog.setVisible(true);
      } else {
        JOptionPane.showMessageDialog(productPanel, "Please select a row to edit.",
            "No Row Selected", JOptionPane.WARNING_MESSAGE);
      }
    });

    searchButton.addActionListener(e -> {
      // Create a custom JDialog with input components for each search criterion
      JDialog searchProductDialog = new JDialog((Frame) null, "Search Product", true);
      searchProductDialog.setSize(300, 200);
      searchProductDialog.setLayout(new GridLayout(5, 2));

      // Create and add input components for each criterion
      JLabel nameLabel = new JLabel("Name:");
      JTextField nameField = new JTextField();
      searchProductDialog.add(nameLabel);
      searchProductDialog.add(nameField);

      JLabel quantityLabel = new JLabel("Quantity:");
      JTextField quantityField = new JTextField();
      ((PlainDocument) quantityField.getDocument()).setDocumentFilter(integerFilter);
      searchProductDialog.add(quantityLabel);
      searchProductDialog.add(quantityField);

      JLabel categoryLabel = new JLabel("Category:");
      JComboBox<String> categorySearchBox = new JComboBox<>(
          categoryController.getAllCategories().stream()
              .map(CategoryDTO::getName)
              .toArray(String[]::new));
      categorySearchBox.insertItemAt("", 0);
      categorySearchBox.setSelectedIndex(0);
      searchProductDialog.add(categoryLabel);
      searchProductDialog.add(categorySearchBox);

      JLabel warehouseLabel = new JLabel("Warehouse:");
      JComboBox<String> warehouseSearchBox = new JComboBox<>(
          warehouseController.getAllWarehouses().stream()
              .map(WarehouseDTO::getName)
              .toArray(String[]::new));
      warehouseSearchBox.insertItemAt("", 0);
      warehouseSearchBox.setSelectedIndex(0);
      searchProductDialog.add(warehouseLabel);
      searchProductDialog.add(warehouseSearchBox);

      // Add OK and Cancel buttons
      JButton okButton = new JButton("Search");
      JButton cancelButton = new JButton("Cancel");
      searchProductDialog.add(okButton);
      searchProductDialog.add(cancelButton);

      // OK button listener
      okButton.addActionListener(event -> {
        String searchName = nameField.getText().trim();
        Integer searchQuantity;
        if (quantityField.getText().trim().isEmpty()) {
          searchQuantity = null;
        } else {
          searchQuantity = Integer.valueOf(quantityField.getText().trim());
        }
        String searchCategory = (String) categorySearchBox.getSelectedItem();
        String searchWarehouse = (String) warehouseSearchBox.getSelectedItem();

        // Search for products using the input values (you need to create this method in the ProductController)
        List<ProductDTO> filteredProducts = productController.searchProducts(searchName,
            searchQuantity, searchCategory, searchWarehouse);

        // Update the JTable with the filtered results
        updateProductTable(productTableModel, filteredProducts);

        // Close the search dialog
        searchProductDialog.dispose();
      });

      // Cancel button listener
      cancelButton.addActionListener(event -> searchProductDialog.dispose());

      searchProductDialog.setVisible(true);
    });

    deleteButton.addActionListener(e -> {
      int selectedRow = productTable.getSelectedRow();

      if (selectedRow >= 0) {
        String currentProductName = (String) productTableModel.getValueAt(selectedRow, 0);
        String currentProductCategoryName = (String) productTableModel.getValueAt(selectedRow, 2);
        String currentProductWarehouseName = (String) productTableModel.getValueAt(selectedRow, 3);

        Category currentCategory = categoryService.findCategoryByName(currentProductCategoryName);
        Warehouse currentWarehouse = warehouseService.findWarehouseByName(
            currentProductWarehouseName);
        Product currentProduct =
            productService.findProductByNameAndCategoryAndWarehouse(currentProductName,
                currentCategory, currentWarehouse);
        productController.deleteProduct(currentProduct.getId());

        updateProductTable(productTableModel);
      }
    });

    return productPanel;
  }

  private void fillCategoryComboBox(JComboBox<String> categoryComboBox) {
    categoryComboBox.removeAllItems(); // Clear the combo box
    List<CategoryDTO> categories = categoryController.getAllCategories();
    for (CategoryDTO category : categories) {
      categoryComboBox.addItem(category.getName());
    }
  }

  private void fillWarehouseComboBox(JComboBox<String> warehouseComboBox) {
    warehouseComboBox.removeAllItems(); // Clear the combo box
    List<WarehouseDTO> warehouses = warehouseController.getAllWarehouses();
    for (WarehouseDTO warehouse : warehouses) {
      warehouseComboBox.addItem(warehouse.getName());
    }
  }

  private JPanel createWarehousePanel() {
    JPanel warehousePanel = new JPanel(new BorderLayout());

    // Create and set up the JTable
    DefaultTableModel warehouseTableModel = new DefaultTableModel(new Object[][]{},
        new String[]{"Name"});
    JTable warehouseTable = new JTable(warehouseTableModel);

    //Setting the editor in order to forbid double click editing of the rows
    warehouseTable.setDefaultEditor(Object.class, editor);
    JScrollPane warehouseTableScrollPane = new JScrollPane(warehouseTable);
    warehousePanel.add(warehouseTableScrollPane, BorderLayout.CENTER);

    // Create the input panel with JLabels and JTextFields
    JPanel inputPanel = new JPanel(new GridLayout(1, 2));
    inputPanel.add(new JLabel("Warehouse Name:"));
    JTextField warehouseNameField = new JTextField();
    inputPanel.add(warehouseNameField);
    warehousePanel.add(inputPanel, BorderLayout.NORTH);

    // Create the buttons panel with JButtons for CRUD operations and search
    JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));
    JButton addButton = new JButton("Add");
    JButton editButton = new JButton("Edit");
    JButton deleteButton = new JButton("Delete");
    buttonsPanel.add(addButton);
    buttonsPanel.add(editButton);
    buttonsPanel.add(deleteButton);
    warehousePanel.add(buttonsPanel, BorderLayout.SOUTH);

    // Add button listener and other listeners as needed
    addButton.addActionListener(e -> {
      String warehouseName = warehouseNameField.getText();
      if (!warehouseName.trim().isEmpty()) {
        WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setName(warehouseName);
        warehouseController.createWarehouse(warehouseDTO);
        updateWarehouseTable(warehouseTableModel);
      }
    });

    editButton.addActionListener(e -> {
      int selectedRow = warehouseTable.getSelectedRow();
      if (selectedRow != -1) {
        String currentWarehouseName = (String) warehouseTableModel.getValueAt(selectedRow, 0);

        // Create a dialog to allow the user to edit the warehouse name
        String newWarehouseName = (String) JOptionPane.showInputDialog(
            warehousePanel,
            "Edit Warehouse Name:",
            "Edit Warehouse",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            currentWarehouseName
        );

        // If the user has entered a new warehouse name and it's different from the old one
        if (newWarehouseName != null && !newWarehouseName.trim().isEmpty()
            && !newWarehouseName.equals(
            currentWarehouseName)) {
          WarehouseDTO warehouseDTO = warehouseController.getWarehouseByName(currentWarehouseName);
          warehouseDTO.setName(newWarehouseName);
          warehouseController.updateWarehouse(currentWarehouseName, warehouseDTO);

          // Update the JTable with new data
          updateWarehouseTable(warehouseTableModel);
        }
      } else {
        JOptionPane.showMessageDialog(warehousePanel, "Please select a row to edit.",
            "No Row Selected", JOptionPane.WARNING_MESSAGE);
      }
    });

    deleteButton.addActionListener(e -> {
      int selectedRow = warehouseTable.getSelectedRow();

      if (selectedRow >= 0) {
        String currentWarehouseName = (String) warehouseTableModel.getValueAt(selectedRow, 0);

        try {
          warehouseController.deleteWarehouse(currentWarehouseName);
        } catch (NotFoundException ex) {
          throw new RuntimeException(ex.getMessage());
        }

        updateWarehouseTable(warehouseTableModel);
      }
    });

    return warehousePanel;
  }


  private void updateCategoryTable(DefaultTableModel categoryTableModel) {
    List<CategoryDTO> categories = categoryController.getAllCategories();

    // Clear the table model
    categoryTableModel.setRowCount(0);

    // Add the fetched categories to the table model
    for (CategoryDTO category : categories) {
      categoryTableModel.addRow(new Object[]{category.getName()});
    }
  }

  private void updateWarehouseTable(DefaultTableModel warehouseTableModel) {
    List<WarehouseDTO> warehouses = warehouseController.getAllWarehouses();

    // Clear the table model
    warehouseTableModel.setRowCount(0);

    // Add the fetched warehouses to the table model
    for (WarehouseDTO warehouse : warehouses) {
      warehouseTableModel.addRow(new Object[]{warehouse.getName()});
    }
  }

  private void updateProductTable(DefaultTableModel productTableModel) {

    List<ProductDTO> products = productController.getAllProducts();

    // Clear the table model
    productTableModel.setRowCount(0);

    // Add the fetched warehouses to the table model
    for (ProductDTO product : products) {
      productTableModel.addRow(
          new Object[]{product.getName(), product.getQuantity(), product.getCategoryName(),
              product.getWarehouseName()});
    }
  }

  private void updateProductTable(DefaultTableModel productTableModel, List<ProductDTO> products) {
    // Clear the table model
    productTableModel.setRowCount(0);

    // Add the filtered products to the table model
    for (ProductDTO product : products) {
      Object[] rowData = new Object[]{
          product.getName(),
          product.getQuantity(),
          product.getCategoryName(),
          product.getWarehouseName()
      };
      productTableModel.addRow(rowData);
    }
  }
}