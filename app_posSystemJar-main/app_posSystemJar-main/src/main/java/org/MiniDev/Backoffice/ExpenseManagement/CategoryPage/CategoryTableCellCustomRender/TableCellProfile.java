package org.MiniDev.Backoffice.ExpenseManagement.CategoryPage.CategoryTableCellCustomRender;

import javaswingdev.picturebox.DefaultPictureBoxRender;
import org.MiniDev.OOP.EmployeeHistoryLists;
import org.MiniDev.OOP.ExpenseCategoryLists;

import java.awt.*;

public class TableCellProfile extends javax.swing.JPanel {

    private javaswingdev.picturebox.PictureBox pic; // PictureBox for the icon

    public TableCellProfile(ExpenseCategoryLists expenseCategoryLists) {
        initComponents();
        // Set the image if available
        if (expenseCategoryLists.getIconExpenseAsIcon() != null) {
            pic.setImage(expenseCategoryLists.getIconExpenseAsIcon());
        }

        // Custom rendering for the picture box (rectangular)
        pic.setPictureBoxRender(new DefaultPictureBoxRender() {
            @Override
            public Shape render(Rectangle rec) {
                // Return a rectangle shape instead of a rounded shape
                return new Rectangle(rec.x, rec.y, rec.width, rec.height);
            }
        });
    }

    public TableCellProfile(EmployeeHistoryLists empHistLists) {
        initComponents();
        // Set the image if available
        if (empHistLists.getEmpProfileAsIcon() != null) {
            pic.setImage(empHistLists.getEmpProfileAsIcon());
        }

        // Custom rendering for the picture box (rectangular)
        pic.setPictureBoxRender(new DefaultPictureBoxRender() {
            @Override
            public Shape render(Rectangle rec) {
                // Return a rectangle shape instead of a rounded shape
                return new Rectangle(rec.x, rec.y, rec.width, rec.height);
            }
        });
    }

    private void initComponents() {

        pic = new javaswingdev.picturebox.PictureBox(); // Initialize PictureBox

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(pic, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pic, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
}
