# An Efficient Approach Towards Index Structures for Skyline Queries

## Introduce
  Given a multidimensional dataset, a skyline query aims to find all objects that are not dominated by any other object 
  in the dataset. If an object 𝐴 is not worse than 𝐴′ in any dimension, and there is at least one dimension in which 𝐴 
  is better than 𝐴′, we say that 𝐴 dominates 𝐴′. The skyline query represents a typical multi-objective optimization problem 
  and is widely applied in fields such as data analysis, decision support, intelligent recommendation, network optimization, 
  and healthcare.

  &nbsp;&nbsp;&nbsp;&nbsp;Many index-based algorithms focus on reducing dominance tests using additional data structures but ignore further 
  exploration of the original structure for data storage. After all, the cost of build-ing additional data structures is very
  high. In fact, there is no necessity for additional data structures to reduce the dominance tests in skyline queries for 
  most index-based structures. Because in most index structures, data is organized in a certain order. 

  &nbsp;&nbsp;&nbsp;&nbsp;**U-INDG** approach is an index-oriented framework for skyline queries, that can significantly reduce the cost of dominance 
  tests. All of the code necessary to repeat experiments from the paper are available in this suite. Extensive experimental 
  results demonstrate that **U-INDG** shows significant improvements when extended to multiple index structures, and the 
  query efficiency across various datasets outperforms that of state-of-the-art algorithms.

  If you use this code in your research, please cite the following paper: Song, H., Wu, Z., Guo, B. et al. An efficient approach towards index structures for skyline queries. J. King Saud Univ. Comput. Inf. Sci. 37, 154 (2025). https://doi.org/10.1007/s44443-025-00183-3
  

------------------------------------
## Algorithms

  The following algorithms have been implemented in **U-INDG**:
  
  * **U-INDG** : Located in [src/myshgs/MyApproaches](src/myshgs/MyApproaches).
    It is an index-oriented framework for skyline queries, which is extend to various index structures, such as Quad-Tree, Quad<sup>∗</sup>-Tree(a variant of Quad-Tree that addresses the low node utilization
    and fan-out issues of Quad-Tree in high-dimensional space ), ZBTree, and R-Tree. These implementations are referred to as 
    UQuad-INDG, UQuad<sup>∗</sup>-INDG, UZB-INDG, and UZR-INDG, respectively.
  
    * **UQuad-INDG** : Located in [src/myshgs/MyApproaches/IQuadTree](src/myshgs/MyApproaches/IQuadTree).
      It is a extended implementation of U-INDG based on the Quad-Tree index structure.
    * **UQuad<sup>∗</sup>-INDG** : Located in [src/myshgs/MyApproaches/IQuadPlusTree](src/myshgs/MyApproaches/IQuadPlusTree).
      It is a extended implementation of U-INDG based on the Quad<sup>∗</sup>-Tree index structure.
    * **UZB-INDG** : Located in [src/myshgs/MyApproaches/IZSearch](src/myshgs/MyApproaches/IZSearch).
      It is a extended implementation of U-INDG based on the ZBTree index structure.
    * **UZR-INDG** : Located in [src/myshgs/MyApproaches/IZOrderRTree](src/myshgs/MyApproaches/IZOrderRTree).
      It is a extended implementation of U-INDG based on the R-Tree((based on Z-order)) index structure.
  
  All algorithms were implemented in Java with jdk 17.0.1.

------------------------------------

## Datasets

  These datasets contain correlated, anti-correlated, independent, and intensive datasets.

  &nbsp;&nbsp;&nbsp;&nbsp;The synthetic workloads can be generated in [src/myshgs/Utils.java](src/myshgs/Utils.java). Please note that since ZBTree cannot handle floating point data well, in order to achieve a more fair comparison, both real and generated data are integers. 
  In all experiments,values of all datasets are normalized in a [0, 10<sup>9</sup>]<sup>d</sup> space.

------------------------------------

### Requirements

  *U-INDG* only depends on jdk 17.0.1.

------------------------------------

### Usage

1. Download the project

    ```
    git clone https://github.com/shg194/U-INDG.git
    ```

2. Configure Java environment variables
   ```
   jdk 17.0.1.
   ```
3. Open the project using IDEA (Integrated Development Environment).
4. Configure project jdk.

------------------------------------

### References

1. Ken C.K. Lee, Wang Chien Lee, Baihua Zheng, Huajing Li, and Yuan Tian. 2010.
Z-SKY: An efficient skyline query processing framework based on Z-order. VLDB
Journal 19, 3 (2010), 333–362. https://doi.org/10.1007/s00778-009-0166-x

2. Dimitris Papadias, Yufei Tao, Greg Fu, and Bernhard Seeger. 2003. An optimal and
progressive algorithm for skyline queries. In Proceedings of the 2003 ACM SIGMOD
International Conference on Management of Data (San Diego, California) (SIGMOD
’03). Association for Computing Machinery, New York, NY, USA, 467–478. https://doi.org/10.1145/872757.872814

3. Ji Zhang, Wenlu Wang, Xunfei Jiang, Wei-Shinn Ku, and Hua Lu. 2019. An
MBR-Oriented Approach for Efficient Skyline Query Processing. In 2019 IEEE
35th International Conference on Data Engineering (ICDE). 806–817. https://doi.org/10.1109/ICDE.2019.00077
