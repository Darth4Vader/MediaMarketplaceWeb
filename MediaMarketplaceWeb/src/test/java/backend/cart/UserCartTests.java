package backend.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import backend.UserSpringTest;
import backend.dtos.CartDto;
import backend.dtos.references.CartProductReference;
import backend.utils.PurchaseType;

@Transactional
public class UserCartTests extends UserSpringTest {
    
    @Test
    public void addToCartTest() throws Exception {
    	final Long productId = 30L;
    	CartProductReference cartProductReference = new CartProductReference();
    	cartProductReference.setProductId(productId);
    	cartProductReference.setPurchaseType(PurchaseType.BUY.getType());
    	
    	// The product is already inside the cart
    	Thread.sleep(3000);
    	addToCartTest(cartProductReference, status().isConflict());
    	
    	// Delete product from cart
    	deleteFromCartTest(productId, status().isOk());
    	
    	// after deleting the product, it should not be in the cart
    	deleteFromCartTest(productId, status().isNotFound());
    	
    	// check if the product is deleted
    	CartDto cart = getCartTest();
    	assertThat(cart.getCartProducts().stream()
    		.map(cp -> cp.getProduct())
    		.anyMatch(p -> p.getId().equals(productId)))
    	.as("The product is still in the cart").isFalse();
    	
    	// add the product back to the cart
    	CartProductReference cartProductReference2 = new CartProductReference();
    	cartProductReference2.setProductId(productId);
    	cartProductReference2.setPurchaseType(PurchaseType.BUY.getType());
    	addToCartTest(cartProductReference2, status().isCreated());
    }
    
    private CartDto getCartTest() throws Exception {
		ResultActions a = getWithAuthTest("/api/users/carts/", status().isOk());
		return asObject(a.andReturn().getResponse().getContentAsString(), CartDto.class);
	}
    
    private ResultActions addToCartTest(CartProductReference cartProductReference, ResultMatcher matcher) throws Exception {
		return postObjectJsonWithAuthTest("/api/users/carts/", cartProductReference, matcher);
	}
    
    private ResultActions deleteFromCartTest(Long productId, ResultMatcher matcher) throws Exception {
    	return deleteWithArgsAndAuthTest(matcher, "/api/users/carts/{personId}", productId);
    }
}