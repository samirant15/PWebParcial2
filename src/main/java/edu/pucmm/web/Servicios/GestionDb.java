package edu.pucmm.web.Servicios;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by vacax on 03/06/16.
 */
public class GestionDb<T> {

    private static EntityManagerFactory emf;
    private Class<T> claseEntidad;


    public GestionDb(Class<T> claseEntidad) {
        if(emf == null) {
            emf = Persistence.createEntityManagerFactory("MiUnidadPersistencia");
        }
        this.claseEntidad = claseEntidad;

    }

    public EntityManager getEntityManager(){
        return emf.createEntityManager();
    }

    /**
     *
     * @param entidad
     */
    public void crear(T entidad){
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(entidad);
            em.getTransaction().commit();
        }catch (Exception ex){
            em.getTransaction().rollback();
            throw  ex;
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param entidad
     */
    public void editar(T entidad){
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            em.merge(entidad);
            em.getTransaction().commit();
        }catch (Exception ex){
            em.getTransaction().rollback();
            throw  ex;
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param entidad
     */
    public void eliminar(T entidad){
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
//            em.remove(entidad);
            em.remove(em.contains(entidad) ? entidad : em.merge(entidad));
            em.getTransaction().commit();
        }catch (Exception ex){
            em.getTransaction().rollback();
            throw  ex;
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public T find(Object id) {
        EntityManager em = getEntityManager();
        try{
            return em.find(claseEntidad, id);
        } catch (Exception ex){
            throw  ex;
        } finally {
            em.close();
        }
    }

    /**
     *
     * @return
     */
    public List<T> findAll(){
        EntityManager em = getEntityManager();
        try{
            CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(claseEntidad);
            criteriaQuery.select(criteriaQuery.from(claseEntidad));
            return em.createQuery(criteriaQuery).getResultList();
        } catch (Exception ex){
            throw  ex;
        }finally {
            em.close();
        }
    }

    public T findWhere(List<String> columna, List<String> valor){
        EntityManager em = getEntityManager();
        try{
            CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(claseEntidad);
            Root<T> from = criteriaQuery.from(claseEntidad);
            criteriaQuery.select(from);

            for (int i=0; i< columna.size(); i++){
                criteriaQuery.where(em.getCriteriaBuilder().equal(from.get(columna.get(i)),valor.get(i)));
            }

            List<T> res = em.createQuery(criteriaQuery).getResultList();
            if(res.size() > 0)
                return res.get(0);
            else
                return null;
        } catch (Exception ex){
            throw  ex;
        }finally {
            em.close();
        }
    }
    @SuppressWarnings("Duplicates")
    public List<T> findAllOrdered(String order, String... propertiesOrder) {
        EntityManager em = getEntityManager();
        try{
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(claseEntidad);
            Root<T> from = criteriaQuery.from(claseEntidad);
            criteriaQuery.select(from);
            for (String propertyOrder : propertiesOrder) {
                if (order.equals("ASC")) {
                    criteriaQuery.orderBy(cb.asc(from.get(propertyOrder)));
                } else {
                    criteriaQuery.orderBy(cb.desc(from.get(propertyOrder)));
                }
            }
            return em.createQuery(criteriaQuery).getResultList();
        } catch (Exception ex){
            throw  ex;
        }finally {
            em.close();
        }
    }
    @SuppressWarnings("Duplicates")
    public List<T> findPaginationOrdered(int page, int size, String order, String... propertiesOrder){
        EntityManager em = getEntityManager();
        try{
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(claseEntidad);
            Root<T> from = criteriaQuery.from(claseEntidad);
            CriteriaQuery<T> select = criteriaQuery.select(from);
            for (String propertyOrder : propertiesOrder) {
                if (order.equals("ASC")) {
                    criteriaQuery.orderBy(cb.asc(from.get(propertyOrder)));
                } else {
                    criteriaQuery.orderBy(cb.desc(from.get(propertyOrder)));
                }
            }
            Query query = em.createQuery(criteriaQuery);
            query.setFirstResult((page-1) * size);
            query.setMaxResults(size);
            return  query.getResultList();
        } catch (Exception ex){
            throw  ex;
        }finally {
            em.close();
        }
    }
    @SuppressWarnings("Duplicates")
    public List<T> findPagination(int page, int size){
        EntityManager em = getEntityManager();
        try{
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(claseEntidad);
            Root<T> from = criteriaQuery.from(claseEntidad);
            CriteriaQuery<T> select = criteriaQuery.select(from);
            Query query = em.createQuery(select);
            query.setFirstResult((page-1) * size);
            query.setMaxResults(size);
            return  query.getResultList();
        } catch (Exception ex){
            throw  ex;
        }finally {
            em.close();
        }
    }

    public float count(){
        EntityManager em = getEntityManager();
        try{
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = qb.createQuery(Long.class);
            cq.select(qb.count(cq.from(claseEntidad)));
            return em.createQuery(cq).getSingleResult();
        } catch (Exception ex){
            throw  ex;
        }finally {
            em.close();
        }
    }
}