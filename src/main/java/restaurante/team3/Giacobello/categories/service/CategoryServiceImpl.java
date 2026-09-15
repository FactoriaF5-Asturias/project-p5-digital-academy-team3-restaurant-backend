package restaurante.team3.Giacobello.categories.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import restaurante.team3.Giacobello.categories.dto.CategoryDTORequest;
import restaurante.team3.Giacobello.categories.dto.CategoryDTOResponse;
import restaurante.team3.Giacobello.categories.entity.CategoryEntity;
import restaurante.team3.Giacobello.categories.exceptions.CategoryAlreadyExistsException;
import restaurante.team3.Giacobello.categories.exceptions.CategoryNotFoundException;
import restaurante.team3.Giacobello.categories.mappers.CategoryMapper;
import restaurante.team3.Giacobello.categories.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTOResponse> findAll() {
        return categoryRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTOResponse findById(Integer id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id : " + id));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryDTOResponse create(CategoryDTORequest request) {

        if (categoryRepository.existsByName(request.name())) {
            throw new CategoryAlreadyExistsException("Category already exists with name: " + request.name());
        }

        CategoryEntity entity = categoryMapper.toEntity(request);
        CategoryEntity saved = categoryRepository.save(entity);

        return categoryMapper.toResponse(saved);
    }
}
