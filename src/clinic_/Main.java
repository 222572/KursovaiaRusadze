package clinic_;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Enumeration;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("CRUD Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            frame.setLayout(new BorderLayout());

            JPanel crudPanel = new JPanel();
            crudPanel.setBorder(BorderFactory.createTitledBorder("CRUD Options"));
            ButtonGroup crudGroup = new ButtonGroup();
            String[] crudOptions = {"Create", "Read", "Update", "Delete"};
            for (String option : crudOptions) {
                JRadioButton button = new JRadioButton(option);
                crudGroup.add(button);
                crudPanel.add(button);
                if (option.equals("Create")) button.setSelected(true);
            }

            JPanel entityPanel = new JPanel();
            entityPanel.setBorder(BorderFactory.createTitledBorder("Entity Options"));
            ButtonGroup entityGroup = new ButtonGroup();
            String[] entities = {"Patient", "Appointment", "Doctor", "Record"};
            for (String entity : entities) {
                JRadioButton button = new JRadioButton(entity);
                entityGroup.add(button);
                entityPanel.add(button);
                if (entity.equals("Patient")) button.setSelected(true);
            }

            JPanel entryPanel = new JPanel();
            entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.Y_AXIS));
            JTextField[] textFields = new JTextField[5];
            for (int i = 0; i < 5; i++) {
                JTextField textField = new JTextField(20);
                textField.setDocument(new LimitedDocument(100));
                textFields[i] = textField;
                entryPanel.add(textField);
                entryPanel.add(Box.createVerticalStrut(5));
            }

            JButton runButton = new JButton("Run");
            runButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        printEnteredData(crudGroup, entityGroup, textFields);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            frame.add(crudPanel, BorderLayout.NORTH);
            frame.add(entityPanel, BorderLayout.CENTER);
            frame.add(entryPanel, BorderLayout.EAST);
            frame.add(runButton, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }

    private static String getSelectedButtonText(ButtonGroup group) {
        for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }

    private static void printEnteredData(ButtonGroup crudGroup, ButtonGroup entityGroup, JTextField[] textFields) throws SQLException {
        System.out.println("Selected CRUD Option: " + getSelectedButtonText(crudGroup));
        System.out.println("Selected Entity Option: " + getSelectedButtonText(entityGroup));
        System.out.println("Entries:");
        for (int i = 0; i < textFields.length; i++) {
            System.out.println("  Entry " + (i + 1) + ": " + textFields[i].getText());
        }

        String choosen_crud_operation = getSelectedButtonText(crudGroup);
        String choosen_table = getSelectedButtonText(entityGroup);

        if (choosen_crud_operation == "Create") {
            if (choosen_table == "Patient") {
                Patient new_patient = new Patient(
                        textFields[0].getText(),
                        textFields[1].getText(),
                        textFields[2].getText(),
                        textFields[3].getText(),
                        textFields[4].getText()
                );
                DatabaseConnection databaseConnection = new DatabaseConnection();
                PatientDAO patientDAO = new PatientDAO(databaseConnection.getConnection());
                try {
                    patientDAO.addPatient(new_patient);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choosen_table == "Doctor") {
                Doctor doctor = new Doctor(
                        textFields[0].getText(),
                        textFields[1].getText(),
                        textFields[2].getText(),
                        Integer.parseInt(textFields[3].getText()),
                        textFields[4].getText()
                );
                DatabaseConnection databaseConnection = new DatabaseConnection();
                DoctorDAO doctorDAO = new DoctorDAO(databaseConnection.getConnection());
                try {
                    doctorDAO.addDoctor(doctor);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choosen_table == "Appointment") {
                Appointment appointment = new Appointment(
                        textFields[0].getText(),
                        textFields[1].getText(),
                        textFields[2].getText(),
                        Integer.parseInt(textFields[3].getText()),
                        textFields[4].getText()
                );
                DatabaseConnection databaseConnection = new DatabaseConnection();
                AppointmentDAO appointmentDAO = new AppointmentDAO(databaseConnection.getConnection());
                try {
                    appointmentDAO.addAppointment(appointment);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choosen_table == "Record") {
                MedicalRecord medicalRecord = new MedicalRecord(
                        textFields[0].getText(),
                        textFields[1].getText(),
                        textFields[2].getText(),
                        textFields[3].getText(),
                        textFields[4].getText()
                                        );
                DatabaseConnection databaseConnection = new DatabaseConnection();
                MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO(databaseConnection.getConnection());
                medicalRecordDAO.addMedicalRecord(medicalRecord);
            }
        } else if (choosen_crud_operation == "Read") {
            if (choosen_table == "Patient") {
                DatabaseConnection databaseConnection = new DatabaseConnection();
                PatientDAO patientDAO = new PatientDAO(databaseConnection.getConnection());
                System.out.println(patientDAO.getPatientByName(textFields[0].getText()));
            } else if (choosen_table == "Doctor") {
                DatabaseConnection databaseConnection = new DatabaseConnection();
                DoctorDAO doctorDAO = new DoctorDAO(databaseConnection.getConnection());
                System.out.println(doctorDAO.getDoctorByName(textFields[0].getText()));
            } else if (choosen_table == "Appointment") {
                DatabaseConnection databaseConnection = new DatabaseConnection();
                AppointmentDAO appointmentDAO = new AppointmentDAO(databaseConnection.getConnection());
                System.out.println(appointmentDAO.getAppointment(textFields[0].getText(), textFields[1].getText()));
            } else if (choosen_table == "Record") {
                DatabaseConnection databaseConnection = new DatabaseConnection();
                MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO(databaseConnection.getConnection());
                System.out.println(medicalRecordDAO.getMedicalRecordsByPatientName(textFields[0].getText()));
            }
        } else if (choosen_crud_operation == "Update") {
            if (choosen_table == "Patient") {
                Patient new_patient = new Patient(
                        textFields[0].getText(),
                        textFields[1].getText(),
                        textFields[2].getText(),
                        textFields[3].getText(),
                        textFields[4].getText()
                );
                DatabaseConnection databaseConnection = new DatabaseConnection();
                PatientDAO patientDAO = new PatientDAO(databaseConnection.getConnection());
                try {
                    patientDAO.updatePatient(new_patient);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choosen_table == "Doctor") {
                Doctor doctor = new Doctor(
                        textFields[0].getText(),
                        textFields[1].getText(),
                        textFields[2].getText(),
                        Integer.parseInt(textFields[3].getText()),
                        textFields[4].getText()
                );
                DatabaseConnection databaseConnection = new DatabaseConnection();
                DoctorDAO doctorDAO = new DoctorDAO(databaseConnection.getConnection());
                try {
                    doctorDAO.updateDoctor(doctor);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choosen_table == "Appointment") {
                Appointment appointment = new Appointment(
                        textFields[0].getText(),
                        textFields[1].getText(),
                        textFields[2].getText(),
                        Integer.parseInt(textFields[3].getText()),
                        textFields[4].getText()
                        );
                DatabaseConnection databaseConnection = new DatabaseConnection();
                AppointmentDAO appointmentDAO = new AppointmentDAO(databaseConnection.getConnection());
                try {
                    appointmentDAO.updateAppointment(appointment);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choosen_table == "Record") {
                MedicalRecord medicalRecord = new MedicalRecord(
                        textFields[0].getText(),
                        textFields[1].getText(),
                        textFields[2].getText(),
                        textFields[3].getText(),
                        textFields[4].getText()
                );
                DatabaseConnection databaseConnection = new DatabaseConnection();
                MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO(databaseConnection.getConnection());
                try {
                    medicalRecordDAO.updateMedicalRecord(medicalRecord);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (choosen_crud_operation == "Delete") {
            if (choosen_table == "Patient") {
                DatabaseConnection databaseConnection = new DatabaseConnection();
                PatientDAO patientDAO = new PatientDAO(databaseConnection.getConnection());
                patientDAO.deletePatient(textFields[0].getText());
            } else if (choosen_table == "Doctor") {
                DatabaseConnection databaseConnection = new DatabaseConnection();
                DoctorDAO doctorDAO = new DoctorDAO(databaseConnection.getConnection());
                doctorDAO.deleteDoctor(textFields[0].getText());
            } else if (choosen_table == "Appointment") {
                DatabaseConnection databaseConnection = new DatabaseConnection();
                AppointmentDAO appointmentDAO = new AppointmentDAO(databaseConnection.getConnection());
                appointmentDAO.deleteAppointment(textFields[0].getText(), textFields[1].getText());
            } else if (choosen_table == "Record") {
                DatabaseConnection databaseConnection = new DatabaseConnection();
                MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO(databaseConnection.getConnection());
                medicalRecordDAO.deleteMedicalRecord(textFields[0].getText(), textFields[1].getText());
            }
        } else {

        }

    }

    static class LimitedDocument extends javax.swing.text.PlainDocument {
        private int limit;

        public LimitedDocument(int limit) {
            this.limit = limit;
        }

        @Override
        public void insertString(int offset, String str, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
            if (str == null || getLength() + str.length() > limit) {
                return;
            }
            super.insertString(offset, str, attr);
        }
    }
}

