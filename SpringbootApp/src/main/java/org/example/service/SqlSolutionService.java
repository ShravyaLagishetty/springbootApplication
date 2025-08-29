package com.example.bfh.service;

import org.springframework.stereotype.Service;

@Service
public class SqlSolutionService {

    // ---- Question 2 (Even regNo) ----
    // From your SQL PDF: count employees younger than each employee, inside the same department,
    // and order by EMP_ID desc. (Uses DOB; later date => younger)
    private static final String SQL_Q2_EVEN =
            "SELECT e1.EMP_ID,\n" +
                    "       e1.FIRST_NAME,\n" +
                    "       e1.LAST_NAME,\n" +
                    "       d.DEPARTMENT_NAME,\n" +
                    "       COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT\n" +
                    "FROM EMPLOYEE e1\n" +
                    "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID\n" +
                    "LEFT JOIN EMPLOYEE e2\n" +
                    "  ON e1.DEPARTMENT = e2.DEPARTMENT\n" +
                    " AND e2.DOB > e1.DOB\n" +
                    "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME\n" +
                    "ORDER BY e1.EMP_ID DESC;";

    // ---- Question 1 (Odd regNo) ----
    // Replace the below with the correct SQL for Q1 from your assigned PDF link.
    // Leaving a clear marker so you can paste it and commit.
    private static final String SQL_Q1_ODD =
            "-- TODO: Paste your final SQL for Question 1 here\n" +
                    "SELECT 1;";

    public String resolveFinalSql(String regNo) {
        if (regNo == null || regNo.isBlank()) {
            throw new IllegalArgumentException("regNo must not be blank");
        }
        String digits = regNo.replaceAll("\\D+", "");
        if (digits.length() < 2) {
            throw new IllegalArgumentException("regNo must contain at least two digits at the end (e.g., REG12345)");
        }
        int lastTwo = Integer.parseInt(digits.substring(digits.length() - 2));
        boolean even = (lastTwo % 2 == 0);
        return even ? SQL_Q2_EVEN : SQL_Q1_ODD;
    }
}
