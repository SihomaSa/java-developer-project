import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getLowStock } from '../services/productService';
import type { ProductSummary } from '../types/product';
import { AlertTriangle } from 'lucide-react';

export default function LowStock() {
  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getLowStock().then(res => {
      setProducts(res.data);
      setLoading(false);
    });
  }, []);

  if (loading) return <div>Cargando...</div>;

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-6 flex items-center">
        <AlertTriangle className="h-6 w-6 text-yellow-500 mr-2" /> Productos con stock bajo (&lt;5)
      </h1>
      {products.length === 0 ? (
        <p className="text-gray-500">No hay productos con stock bajo.</p>
      ) : (
        <div className="bg-white dark:bg-gray-800 shadow overflow-hidden sm:rounded-md">
          <ul className="divide-y divide-gray-200 dark:divide-gray-700">
            {products.map(p => (
              <li key={p.id}>
                <Link to={`/product/${p.id}`} className="block hover:bg-gray-50 dark:hover:bg-gray-700">
                  <div className="px-4 py-4 sm:px-6">
                    <div className="flex items-center justify-between">
                      <p className="text-sm font-medium text-indigo-600 truncate">{p.nombre}</p>
                      <div className="ml-2 flex-shrink-0 flex">
                        <p className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">
                          Stock: {p.stock}
                        </p>
                      </div>
                    </div>
                    <div className="mt-2 sm:flex sm:justify-between">
                      <div className="sm:flex">
                        <p className="flex items-center text-sm text-gray-500">SKU: {p.sku}</p>
                      </div>
                      <div className="mt-2 flex items-center text-sm text-gray-500 sm:mt-0">
                        <p>${p.precio.toLocaleString()}</p>
                      </div>
                    </div>
                  </div>
                </Link>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}