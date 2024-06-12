package entity;

import java.time.LocalDate;

public class Loan {
    private int bookID, userID;
    private LocalDate loanDate,
            expectedReturnDate, //expected date of book return
            actualReturnDate; //actual date on which the book was returned

    public Loan(int bookID, int userID, LocalDate loanDate, LocalDate expectedReturnDate) {
        this.bookID = bookID;
        this.userID = userID;
        this.loanDate = loanDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = null;
    }

    public Loan(int bookID, int userID, LocalDate loanDate, LocalDate expectedReturnDate, LocalDate actualReturnDate) {
        this.bookID = bookID;
        this.userID = userID;
        this.loanDate = loanDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

}
