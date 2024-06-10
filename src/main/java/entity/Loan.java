package entity;

import java.time.LocalDate;

public class Loan {
    private int bookID, userID;
    private LocalDate loanDate, returnDate;

    public Loan(int bookID, int userID, LocalDate loanDate, LocalDate returnDate) {
        this.bookID = bookID;
        this.userID = userID;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
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

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
