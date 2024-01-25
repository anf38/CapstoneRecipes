
package com.example.recipepage;
import com.example.recipepage.model.layout;
public class recipePage {
    public static void main(String[] args) {
        System.out.println("hello world ");

        layout myLayout = new layout ("javafx or swing?");
        myLayout.setModel("both!");
        System.out.println(myLayout.getModel());
    }
}