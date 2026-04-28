import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getProducts, searchProducts } from '../services/productService';
import type { ProductSummary } from '../types/product';
import { Search, Package } from 'lucide-react';

export default function ProductList() {
  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  const loadProducts = async () => {
    setLoading(true);
    try {
      const res = searchTerm
        ? await searchProducts(searchTerm, page)
        : await getProducts(page);
      setProducts(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProducts();
  }, [page, searchTerm]);

  const handleSearch = () => {
    setPage(0);
    setSearchTerm(search);
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Catálogo de productos</h1>
        <div className="flex space-x-2">
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Buscar por nombre..."
            className="px-4 py-2 border rounded-lg dark:bg-gray-800 dark:border-gray-600"
          />
          <button onClick={handleSearch} className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700">
            <Search className="h-5 w-5" />
          </button>
        </div>
      </div>

      {loading ? (
        <div className="flex justify-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {products.map((product) => (
              <Link key={product.id} to={`/product/${product.id}`} className="group">
                <div className="bg-white dark:bg-gray-800 rounded-xl shadow-md overflow-hidden hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
                  <div className="p-6">
                    <div className="flex justify-between items-start">
                      <Package className="h-8 w-8 text-indigo-500" />
                      <span className={`px-2 py-1 text-xs rounded-full ${product.estado === 'ACTIVO' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                        {product.estado}
                      </span>
                    </div>
                    <h3 className="mt-4 text-lg font-semibold text-gray-900 dark:text-white line-clamp-1">{product.nombre}</h3>
                    <p className="text-sm text-gray-500 dark:text-gray-400">SKU: {product.sku}</p>
                    <div className="mt-4 flex justify-between items-center">
                      <span className="text-xl font-bold text-indigo-600">${product.precio.toLocaleString()}</span>
                      <span className="text-sm text-gray-500">Stock: {product.stock}</span>
                    </div>
                  </div>
                </div>
              </Link>
            ))}
          </div>

          {totalPages > 1 && (
            <div className="flex justify-center mt-8 space-x-2">
              <button disabled={page === 0} onClick={() => setPage(p => p - 1)} className="px-4 py-2 border rounded-lg disabled:opacity-50">Anterior</button>
              <span className="px-4 py-2">Página {page + 1} de {totalPages}</span>
              <button disabled={page + 1 >= totalPages} onClick={() => setPage(p => p + 1)} className="px-4 py-2 border rounded-lg disabled:opacity-50">Siguiente</button>
            </div>
          )}
        </>
      )}
    </div>
  );
}