import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

/** This class represents a Dictionary,
 * You can search for an expressions.
 * You can load expressions from a file, add expressions, delete, update and save to file
 * **/

public class Dictionary extends JFrame implements ActionListener {

    private final HashMap<String,String> dictionary; // expression's list as keys, the value is the meaning
    private final JButton load = new JButton("Load file");
    private final JButton save = new JButton("Save file");
    private final JButton add = new JButton("Add Expression");
    private final JButton delete = new JButton("Delete Expression");
    private final JButton update = new JButton("Update Expression");
    private final JButton search = new JButton("Search Expression");
    private final JList <Object> allExp; // expressions list
    private final DefaultListModel <Object> model; // model for the list
    private final JTextArea text; // text area showing expressions
    private final JPanel expressions; // holding the text area and the list
    private final JScrollPane moreExp; // scroll for the list

    public Dictionary()
    {
        super("Dictionary");
        dictionary = new HashMap<>();
        JPanel panel = new JPanel();
        JPanel searchPanel = new JPanel();
        JPanel buttons = new JPanel();
        JPanel fileButtons = new JPanel();
        JPanel updateButtons = new JPanel();
        JPanel textArea = new JPanel();
        expressions = new JPanel();
        text = new JTextArea("Welcome to your Dictionary",50,20);
        model = new DefaultListModel<>();
        allExp = new JList<>(model);
        moreExp = new JScrollPane(allExp);
        setSize(700,400);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        searchPanel.setLayout(new BorderLayout());
        buttons.setLayout(new BorderLayout());
        panel.setLayout(new BorderLayout());
        updateButtons.setLayout(new BorderLayout());
        fileButtons.setLayout(new BorderLayout());
        textArea.setLayout(new GridLayout(0,2));

        // Adding panels, buttons and listeners

        load.addActionListener(this);
        save.addActionListener(this);
        add.addActionListener(this);
        delete.addActionListener(this);
        update.addActionListener(this);
        search.addActionListener(this);

        expressions.add(moreExp);
        moreExp.setPreferredSize(new Dimension(expressions.getWidth(),expressions.getHeight()));

        allExp.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String exp = (String)allExp.getSelectedValue();
                displayExp(exp);
            }
        });

        allExp.setVisible(true);
        allExp.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        textArea.add(expressions);
        textArea.add(text);

        fileButtons.add(load,BorderLayout.NORTH);
        fileButtons.add(save,BorderLayout.SOUTH);

        updateButtons.add(add,BorderLayout.NORTH);
        updateButtons.add(update,BorderLayout.CENTER);
        updateButtons.add(delete,BorderLayout.SOUTH);

        buttons.add(updateButtons,BorderLayout.EAST);
        buttons.add(fileButtons,BorderLayout.WEST);

        searchPanel.add(textArea,BorderLayout.CENTER);
        searchPanel.add(search,BorderLayout.AFTER_LAST_LINE);
        panel.add(searchPanel);
        panel.add(buttons,BorderLayout.SOUTH);
        add(panel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==load)
            load();
        if(e.getSource()==save) {
            save();
        }
        if(e.getSource()==search)
            search();
        if(e.getSource()==add)
            add();
        if(e.getSource()==delete)
            delete();
        if(e.getSource()==update)
            update();
    }

    // Loading the input from a file. fist line is expression, second line is meaning
    private void load()
    {
        File input;
        JFileChooser fileChooser = new JFileChooser();
        int res;
        int line = 0;
        Scanner s;
        String exp = "";
        String mean = "";
        res = fileChooser.showOpenDialog(this);
        if(res == JFileChooser.CANCEL_OPTION)
            return;
        input = fileChooser.getSelectedFile();

        try {
            s = new Scanner(input);
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
            return;
        }

        while (s.hasNext()) // scanning all expressions
        {
            if(line == 0) // expression
            {
                exp = s.nextLine();
            }
            else if(line == 1) // meaning
            {
                mean = s.nextLine();
            }
            line ++;
            if(line == 2) // end of expression and meaning
            {
                addExp(exp,mean);
                line = 0;
            }
        }
        s.close();
        sort();
    }

    // saving the dictionary to file
    public void save()  {

        JFileChooser fileChooser = new JFileChooser();
        int res = fileChooser.showSaveDialog(this);
        if(res == JFileChooser.CANCEL_OPTION)
            return;
        if (res == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            FileWriter fw = null;
            try {
                fw = new FileWriter(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert fw != null;
                fw.write(this.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Adding an expression to the dictionary from user's choice
    public void add()
    {
        String exp;
        String expMean;
        exp = JOptionPane.showInputDialog("Enter Expression:");
        if(exp == null || exp.equals(""))
            return;
        if(dictionary.containsKey(exp)) {
            text.setText(exp + " already exists, you can update it");
        }
        else {
            expMean = JOptionPane.showInputDialog("Enter Meaning:");
            addExp(exp,expMean);
        }
        setVisible(true);
    }

    // Adding an expression from file
    private void addExp(String exp, String mean)
    {
        String upperExp = upperCase(exp); // makes sure the first letter in the expression is capital
        String upperMean = upperCase(mean);
        if(dictionary.containsKey(upperExp)) {
            text.setText(exp + " already exists, you can update it");
        }
        else {
            dictionary.put(upperExp,upperMean);
            text.setText("Expression " + upperExp + " added");
            sort(); // sorting the list with the new expression
        }
        setVisible(true);
    }

    // Changes the first letter in a string to uppercase
    private String upperCase(String s)
    {
        String upper = s.substring(0,1).toUpperCase();
        upper += s.substring(1);

        return upper;
    }

    // Deleting an expression from the dictionary
    public void delete()
    {
        String exp;
        exp = JOptionPane.showInputDialog("Enter Expression to delete:");

        if(exp == null || exp.equals("")) {
            text.setText("Expression not entered");
        }

        else{
            exp = upperCase(exp);
            if(!dictionary.containsKey(exp)) {
                text.setText("Expression " + exp + " does not exist");
                }
            else {
                    dictionary.remove(exp);
                    text.setText(exp + " removed from dictionary");
                    setVisible(true);
                    sort();
                    }
            }
    }

    // Updating an expression's meaning
    public void update()
    {
        String exp;
        String mean;
        exp = JOptionPane.showInputDialog("Enter Expression to update:");

        if(exp == null || exp.equals("")) {
            text.setText("Expression not entered");
        }

        else {
            exp = upperCase(exp);
            if(!dictionary.containsKey(exp)) {
                text.setText("Expression " + exp + " does not exist");
            }
            else {
                mean = JOptionPane.showInputDialog("Enter Expression's new meaning:");
                mean = upperCase(mean);
                dictionary.put(exp,mean);
                sort();
            }
            setVisible(true);
        }
    }

    // Searching for an expression in the dictionary
    public void search()
    {
        String exp;
        exp = JOptionPane.showInputDialog("Enter Expression to search:");

        if(exp == null || exp.equals("")) {
            text.setText("Expression not entered");
        }

        else{
            exp = upperCase(exp);
            if(!dictionary.containsKey(exp)) {
                text.setText("Expression " + exp + " does not exist");
            }
            else {
                displayExp(exp);
            }
            setVisible(true);
        }
    }

    // Sorting the expressions in the displayed list
    private void sort()
    {
        Object [] allKeys;
        SortedSet<String> sortedKeys = new TreeSet<>(dictionary.keySet());
        allKeys =  sortedKeys.toArray();
        model.clear();

        for(Object ob : allKeys)
        {
            model.addElement(ob);
        }

        moreExp.setPreferredSize(new Dimension(expressions.getWidth(),expressions.getHeight()));
        setVisible(true);
    }

    // Displaying expressions and meaning
    private void displayExp(String exp)
    {
        if(exp == null)
        {
            text.setText("Welcome to Dictionary");
        }
        else {
            String mean = dictionary.get(exp);
            text.setText("Expression:\n\n"+exp + "\n\nMeaning:\n\n" + mean);
        }
    }

    // A String representation of the dictionary
    public String toString()
    {
        StringBuilder dic = new StringBuilder();
        Object [] allKeys;
        String exp;
        String mean;
        SortedSet<String> sortedKeys = new TreeSet<>(dictionary.keySet());
        allKeys =  sortedKeys.toArray();
        for(Object ob : allKeys)
        {
            exp = (String)ob;
            mean = dictionary.get(ob);

            dic.append(exp).append("\n").append(mean).append("\n");
        }
        return dic.toString();
    }
}
