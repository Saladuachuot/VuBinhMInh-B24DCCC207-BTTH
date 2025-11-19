package OOP;
import java.util.*;

class DuplicateIdException extends Exception { public DuplicateIdException(String s){super(s);} }
class NotFoundException extends Exception { public NotFoundException(String s){super(s);} }
class InvalidPriceException extends Exception { public InvalidPriceException(String s){super(s);} }
class NonRefundableException extends Exception { public NonRefundableException(String s){super(s);} }

interface Deliverable { void deliver(); }
interface Refundable { void refund() throws NonRefundableException; }
interface Payment { void pay(Order o); }

class Product implements Deliverable {
    String id, name; double price;
    Product(String id, String name, double price) throws InvalidPriceException {
        if (price < 0) throw new InvalidPriceException("Giá âm!");
        this.id=id; this.name=name; this.price=price;
    }
    public void deliver(){ System.out.println("Giao: " + name); }
    public String getId(){return id;}
    public String toString(){return id+"-"+name+"-"+price;}
}
class Book extends Product implements Refundable {
    String author;
    Book(String id, String name, double price, String author) throws InvalidPriceException {
        super(id,name,price); this.author=author;
    }
    public void refund(){ System.out.println("Refund sách: " + name); }
}
class Phone extends Product implements Refundable {
    Phone(String id, String name, double price) throws InvalidPriceException { super(id,name,price); }
    public void refund(){ System.out.println("Refund phone: " + name); }
}
class Laptop extends Product {
    Laptop(String id, String name, double price) throws InvalidPriceException { super(id,name,price); }
}

class Customer {
    String id, name;
    Customer(String id, String name){ this.id=id; this.name=name; }
    public String getId(){return id;}
}

class Order {
    String id; Customer c; List<Product> items = new ArrayList<>();
    Order(String id, Customer c){ this.id=id; this.c=c; }
    void add(Product p){ items.add(p); }
    double total(){ return items.stream().mapToDouble(x->x.price).sum(); }
    public String getId(){return id;}
}

class CreditCardPayment implements Payment {
    public void pay(Order o){ System.out.println("Thanh toán thẻ: " + o.total()); }
}
class PaypalPayment implements Payment {
    public void pay(Order o){ System.out.println("Thanh toán Paypal: " + o.total()); }
}
class CashPayment implements Payment {
    public void pay(Order o){ System.out.println("Thanh toán tiền mặt: " + o.total()); }
}

interface Repository<T>{
    void add(T item) throws DuplicateIdException;
    void update(T item) throws NotFoundException;
    void delete(String id) throws NotFoundException;
    List<T> findAll();
    T findById(String id) throws NotFoundException;
}
class InMemoryRepo<T> implements Repository<T>{
    Map<String,T> map = new HashMap<>();
    String getId(T o){
        try{ return (String) o.getClass().getMethod("getId").invoke(o); }
        catch(Exception e){ return null; }
    }
    public void add(T item) throws DuplicateIdException {
        String id=getId(item);
        if(map.containsKey(id)) throw new DuplicateIdException("Trùng id");
        map.put(id,item);
    }
    public void update(T item) throws NotFoundException {
        String id=getId(item);
        if(!map.containsKey(id)) throw new NotFoundException("Không tìm thấy");
        map.put(id,item);
    }
    public void delete(String id) throws NotFoundException {
        if(!map.containsKey(id)) throw new NotFoundException("Không tìm thấy");
        map.remove(id);
    }
    public List<T> findAll(){ return new ArrayList<>(map.values()); }
    public T findById(String id) throws NotFoundException {
        if(!map.containsKey(id)) throw new NotFoundException("Không tìm thấy");
        return map.get(id);
    }
}

public class Oop {
    public static void main(String[] args) {
        try {
            Repository<Product> products = new InMemoryRepo<>();
            Repository<Customer> customers = new InMemoryRepo<>();
            Repository<Order> orders = new InMemoryRepo<>();

            Product book = new Book("p1","Book A",100,"Author A");
            Product phone = new Phone("p2","Phone A",300);
            Product laptop = new Laptop("p3","Laptop A",900);

            products.add(book);
            products.add(phone);
            products.add(laptop);

            System.out.println("Danh sách sản phẩm:");
            products.findAll().forEach(System.out::println);

            book.deliver();
            phone.deliver();
            laptop.deliver();

            ((Refundable)book).refund();
            ((Refundable)phone).refund();
            try {
                if (laptop instanceof Refundable) ((Refundable)laptop).refund();
                else throw new NonRefundableException("Laptop không refund");
            } catch (NonRefundableException e){
                System.out.println("Lỗi refund: " + e.getMessage());
            }

            Customer c = new Customer("c1","Minh");
            customers.add(c);

            Order o = new Order("o1",c);
            o.add(book); o.add(phone);
            orders.add(o);

            new CreditCardPayment().pay(o);
            new PaypalPayment().pay(o);
            new CashPayment().pay(o);

        } catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}

