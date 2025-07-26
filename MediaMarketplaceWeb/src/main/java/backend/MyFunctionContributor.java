package backend;

import java.time.LocalDateTime;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.BasicType;
import org.hibernate.type.StandardBasicTypes;

public class MyFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
    	// https://stackoverflow.com/questions/72013981/hibernate-6-0-0-final-custom-dialect-not-working-anymore
    	// https://aregall.tech/hibernate-6-custom-functions
    	BasicType<LocalDateTime> resultType = functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.LOCAL_DATE_TIME);
        functionContributions.getFunctionRegistry()
        .registerPattern("date_add_seconds", 
        		"DATE_ADD(?1, INTERVAL ?2 SECOND)", resultType);
    }
}