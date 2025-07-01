# Spring + MongoDB

- - -
## 1. 프로젝트 생성
[Spring Initializr](https://start.spring.io/)에서 다음과 같이 설정함.

### 1.1 기본 설정
- Project: Gradle
- Language: Java
- Spring Boot: 3.5.x
- Packaging: Jar
- Java: 17이상
- 

### 1.2 의존성
- Spring Web
- Spring Data Mongo DB
- Lombok
- Spring Boot DevTools

**Generate** 버튼 클릭 후 프로젝트 압축파일 다운로드 및 IDE에서 열기

- - -
## 2. MongoDB 연결
- MongoDB에서 mydatabase가 생성되어 있어야함.
- MongoDB는 기본적으로 27017 포트를 사용함.

```yaml
spring.application.name=demo-mongo-db
spring:
    data:
        mongodb:
            host: localhost
            port: 27017
            database: mydatabase
```

- - -
## 3. 주요 코드 구성
### 3.1 Document
```java
@Document(collection = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private String id;      // MongoDB가 생성하는 고유 ID
    private String name;    // 상품명
    private int price;      // 가격
    private int stock;      // 재고 수량          
}
```

### 3.2 Repository
```java
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByPriceGreaterThan(int price);
}
```

- MongoRepository를 확장하여, 기본 CRUD 외에 가격이 특정 값 이상인 상품 조회 메서드를 선언함.


### 3.3 Service
- Controller 와 Respository 사이에서 비즈니스 로직을 처리한다.
- 각 메서드는 Repository를 호출하여 데이터 조회, 저장, 수정, 삭제하며, 존재하지 않을 경우에는 예외를 던지거나 Optional을 반환한다.
```java
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 모든 상품 가져오기
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ID로 상품 가져오기
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    // 상품 등록
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // 상품 수정
    public Product updateProduct(String id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedProduct.getName());
                    existing.setPrice(updatedProduct.getPrice());
                    existing.setStock(updatedProduct.getStock());
                    return productRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("[에러] 상품을 찾을 수 없습니다."));
    }

    // 상품 삭제
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}
```

### 3.4 Controller
- HTTP 요청을 받아 Service를 호출하고 JSON 형태로 받아 반환하는 엔드포인트 정의함.
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
```