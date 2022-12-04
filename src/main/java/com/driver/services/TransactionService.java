package com.driver.services;

import com.driver.models.*;
import com.driver.repositories.BookRepository;
import com.driver.repositories.CardRepository;
import com.driver.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    BookRepository bookRepository5;

    @Autowired
    CardRepository cardRepository5;

    @Autowired
    TransactionRepository transactionRepository5;

    @Value("${books.max_allowed}")
    public int max_allowed_books;

    @Value("${books.max_allowed_days}")
    public int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    public int fine_per_day;

    public String issueBook(int cardId, int bookId) throws Exception {
        Book book=bookRepository5.findById(bookId).get();
        Card card5=cardRepository5.findById(cardId).get();

//
        //check whether bookId and cardId already exist

        //conditions required for successful transaction of issue book:
        //1. book is present and available
        // If it fails: throw new Exception("Book is either unavailable or not present");
        if(!bookRepository5.existsById(bookId)){
            throw new Exception("Book is either unavailable or not present");
        }
        //2. card is present and activated
        // If it fails: throw new Exception("Card is invalid");
        if(cardRepository5.existsById(cardId)){
            Card card=cardRepository5.findById(cardId).get();
            if(card.getCardStatus().equals("DEACTIVATED")){
                throw new Exception("Card is invalid");
            }
        }
        //3. number of books issued against the card is strictly less than max_allowed_books
        // If it fails: throw new Exception("Book limit has reached for this card");
        //If the transaction is successful, save the transaction to the list of transactions and return the id
            Book book1=bookRepository5.findById(bookId).get();
            Card card1=cardRepository5.findById(cardId).get();
        if(card1.getBooks().size()>max_allowed_books ){
            throw new Exception("Book limit has reached for this card");

        }else{
            if(card1.getBooks().size()==0){
                List<Book> books=new ArrayList<>();
                books.add(book1);
                card1.setBooks(books);
                book1.setCard(card1);
                book1.setAvailable(false);

            }else{
                card1.getBooks().add(book1);
            }

        }
        cardRepository5.save(card1);
        Transaction transaction=new Transaction();
        transaction.setBook(book1);
        transaction.setCard(card1);
        transaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        transaction.setTransactionDate(new Date());
        transactionRepository5.save(transaction);
        //Note that the error message should match exactly in all cases

       return transaction.getTransactionId(); //return transactionId instead
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception{

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId, TransactionStatus.SUCCESSFUL, true);
        Transaction transaction = transactions.get(transactions.size()- 1);

        //for the given transaction calculate the fine amount considering the book has been returned exactly when this function is called
        //make the book available for other users
        //make a new transaction for return book which contains the fine amount as well

        Date d1=transaction.getTransactionDate();
        Date d2=new Date();
        long diff=d2.getTime()-d1.getTime();
        int diff1=(int)diff;
        if(diff1>15){
            fine_per_day=(fine_per_day)*(diff1-15);
            transaction.setFineAmount(fine_per_day);
        }

        Book book=bookRepository5.findById(bookId).get();
        book.setAvailable(true);
        bookRepository5.save(book);

        Card card=cardRepository5.findById(cardId).get();
        card.setCardStatus(CardStatus.ACTIVATED);


    //new transaction

        Transaction returnBookTransaction  = new Transaction();
        returnBookTransaction.setId(transaction.getId());
        returnBookTransaction.setCard(transaction.getCard());
        returnBookTransaction.setBook(transaction.getBook());
        returnBookTransaction.setIssueOperation(transaction.isIssueOperation());
        returnBookTransaction.setTransactionId(transaction.getTransactionId());
        returnBookTransaction.setTransactionStatus(transaction.getTransactionStatus());
        returnBookTransaction.setTransactionDate(transaction.getTransactionDate());
        returnBookTransaction.setFineAmount(transaction.getFineAmount());
        return returnBookTransaction; //return the transaction after updating all details
    }
}
