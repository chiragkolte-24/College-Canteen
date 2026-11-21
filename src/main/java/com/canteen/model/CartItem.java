package com.canteen.model;
import java.io.Serializable;
public class CartItem implements Serializable {
 public int id, quantity; public String name; public double price;
 public CartItem(int id,String name,double price,int quantity){this.id=id;this.name=name;this.price=price;this.quantity=quantity;}
 public double getSubtotal(){return price*quantity;}
}
