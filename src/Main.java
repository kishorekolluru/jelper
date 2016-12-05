import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
* Example for running Jelper on the example input classes
* */
public class Main {	
	public static void main(String arg[]){
		String get="Dto";
		String set="EusDao";
		//Below code generates setter/getter code for business POJOs
		//set preferred accuracy/mismatch tolerance level
		Jelper.setMatchLevel(Jelper.MATCH_REMAINING_TYPE);
		System.out.println(Jelper.genSetterCode(get,set));

		//below code generates boring JDBC ResultSet Code.
		/*List<String> columns= new ArrayList<String>(Arrays.asList(
				"MODEL_COVERAGE","POLICY_TERM_SK","ACCT_NBR","POL_NBR","TAP_SBU_CD","OGN_EFF_DT","PLN_EXP_DT","CVG_EFFECTIVE_DT","CVG_EXPIRATION_DT","SIC5_KEY","VEHICLE_SK",
				"VEH_UNIT_NBR","ST_CD","TAP_SBU_GRP","SIC_FOUR_DGT_CD","NATURE_OF_BUS","CUSTOMER_SEGMENT","CUSTOMER_SEGMENT_GRP","NON_RC_LIM_RAWSCORE","RC_LIM_RAWSCORE",
				"LIM_BAL_RAWSCORE","XS_RAWSCORE","RAWSCORE","NON_RC_LIM_PRED_LR","RC_LIM_FCT","LIM_BAL_FCT","XS_FCT","PRED_LR","MEAN_PRED_LR","PRED_LRR","MAN_PREM",
				"COV_TERM_MANUAL_PREM","SBU","PRODUCT_SPLIT","STATE","SEGMENT","SUBSEGMENT","POL_MANUAL_PREM","MIN_PRM","MAX_PRM","N_R","FLR_TECH","FLR_BENCH","MIN_PLR_TECH",
				"MAX_PLR_TECH","MIN_PLR_BENCH","MAX_PLR_BENCH","DED_CRDT_FCTR_VEH_COV","TECH_ESTD_LOSS_VEH_COV","PRE_BENCH_ESTD_LOSS_VEH_COV","TECH_PLR_VEH_COV",
				"PRE_BENCH_PLR_VEH_COV","TECH_PRICE_VEH_COV","PRE_BENCH_PRICE_VEH_COV","TECH_PRICE_MDL","PRE_BENCH_PRICE_MDL","QUALITY_SCORE_VEH_COV",
				"QUALITY_SCORE_MDL"

	));*/
		/*try {
			//System.out.println(Jelper.jelperJdbcCodeGen(set, columns));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
