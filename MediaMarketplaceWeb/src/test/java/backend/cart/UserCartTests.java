package backend.cart;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import backend.BaseAuthenticationSpringTest;
import backend.dtos.CartDto;
import backend.dtos.references.CartProductReference;

public class UserCartTests extends BaseAuthenticationSpringTest {

    @Test
    public void getCartTest() throws Exception {
    	ResultActions a = mockMvc
    			.perform(withAuth(MockMvcRequestBuilders
    					.get("/api/users/carts/get")))
    			.andExpect(status().isOk());
    	CartDto cart = asObject(a.andReturn().getResponse().getContentAsString(), CartDto.class);
    }
    
    @Test
    public void addToCartTest() throws Exception {
    	CartProductReference cartProductReference = new CartProductReference();
    	cartProductReference.setProductId(29L);
    	cartProductReference.setBuying(true);
    	ResultActions a = mockMvc
    			.perform(withJsonAndAuth(MockMvcRequestBuilders
    					.post("/api/users/carts/add")
    					, cartProductReference))
    			.andExpect(status().isConflict());
    	//CartDto cart = asObject(a.andReturn().getResponse().getContentAsString(), CartDto.class);
    }

}