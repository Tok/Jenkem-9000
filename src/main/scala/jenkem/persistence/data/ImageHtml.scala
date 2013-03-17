package jenkem.persistence.data

import javax.jdo.annotations.IdentityType
import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.PrimaryKey

/**
 * Persistent HTML data import jenkem.persistence.data.ImagePartTrait
for converted images.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
class ImageHtml(var name: String, var html: String) extends ImagePartTrait {
  @PrimaryKey var _id = name
  override def toString: String = name
}
